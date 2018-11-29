package us.kostenko.architecturecomponentstmdb.details.repository

import androidx.lifecycle.LiveData
import us.kostenko.architecturecomponentstmdb.common.Coroutines
import us.kostenko.architecturecomponentstmdb.details.model.Movie
import us.kostenko.architecturecomponentstmdb.details.repository.persistance.DetailDao
import us.kostenko.architecturecomponentstmdb.details.repository.webservice.MovieWebService
import us.kostenko.architecturecomponentstmdb.details.viewmodel.netres.NetworkBoundResource
import us.kostenko.architecturecomponentstmdb.details.viewmodel.netres.State
import us.kostenko.architecturecomponentstmdb.details.viewmodel.netres.StateAdapter
import java.util.Calendar
import java.util.Date

const val FRESH_TIMEOUT_MINUTES = 1

class MovieDetailRepositoryImpl(private val webService: MovieWebService,
                            private val movieDao: DetailDao,
                            private val coroutines: Coroutines,
                            private val timeout: Int = FRESH_TIMEOUT_MINUTES): MovieDetailRepository {

    val adapter = StateAdapter<Movie>()
    private var movieId = 0

    private val movieResource: NetworkBoundResource<Movie, Movie, State<Movie>> by lazy {
        object : NetworkBoundResource<Movie, Movie, State<Movie>>(coroutines, adapter) {

            override fun saveResult(item: Movie) { movieDao.updateMovie(item, Date()) }

            override fun shouldFetch(data: Movie?) = data?.let { it.dateUpdate < getMaxRefreshTime(Date()) } ?: true

            override fun loadFromDb() = movieDao.getMovie(movieId)

            override suspend fun fetchData(): Movie? = webService.getMovie(movieId).await()

        }
    }

    override fun getMovie(id: Int): LiveData<State<Movie>> {
        movieId = id
        return movieResource.asLiveData()
    }

    override fun retry(id: Int) {
        movieId = if (id > 0) id else movieId
        movieResource.reload()
    }

    override fun like(id: Int, like: Boolean) {
        coroutines {
            movieDao.like(id, like)
        }
    }

    private fun getMaxRefreshTime(currentDate: Date): Date {
        val cal = Calendar.getInstance()
        cal.time = currentDate
        cal.add(Calendar.MINUTE, - timeout)
        return cal.time
    }
}

interface MovieDetailRepository {

    fun getMovie(id: Int): LiveData<State<Movie>>

    fun retry(id: Int = 0)

    fun like(id: Int, like: Boolean)
}