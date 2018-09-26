package us.kostenko.architecturecomponentstmdb.details.repository

import android.arch.lifecycle.LiveData
import kotlinx.coroutines.experimental.GlobalScope
import kotlinx.coroutines.experimental.launch
import us.kostenko.architecturecomponentstmdb.details.model.Movie
import us.kostenko.architecturecomponentstmdb.details.repository.persistance.DetailDao
import us.kostenko.architecturecomponentstmdb.details.repository.webservice.MovieWebService
import java.util.*

const val FRESH_TIMEOUT_MINUTES = 1

class MovieDetailRepository(private val webService: MovieWebService,
                            private val movieDao: DetailDao) {

    fun getMovie(id: Int): LiveData<Movie>  {
        GlobalScope.launch { refreshMovie(id) }
        return movieDao.getMovie(id)
    }

    fun like(id: Int, like: Boolean) {
        GlobalScope.launch {
            movieDao.like(id, like)
        }
    }

    private suspend fun refreshMovie(id: Int) {
        val movieExist = movieDao.hasMovie(id, getMaxRefreshTime(Date())) != null
        if(!movieExist) {
            try {
                val movie = webService.getMovie(id).await()
                movie.dateUpdate = Date()
                movie.apply {
                    movieDao.updateDetail(id, title, releaseDate, posterPath, backdropPath, overview, originalLanguage, originalTitle, genres, dateUpdate)
                }
            } catch (e: Throwable) {
                e.printStackTrace()
            }
        }
    }

    private fun getMaxRefreshTime(currentDate: Date): Date {
        val cal = Calendar.getInstance()
        cal.time = currentDate
        cal.add(Calendar.MINUTE, - FRESH_TIMEOUT_MINUTES)
        return cal.time
    }
}