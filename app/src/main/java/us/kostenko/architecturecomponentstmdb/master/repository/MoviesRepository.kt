package us.kostenko.architecturecomponentstmdb.master.repository

import androidx.lifecycle.LiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import timber.log.Timber
import us.kostenko.architecturecomponentstmdb.master.model.MovieItem
import us.kostenko.architecturecomponentstmdb.master.repository.persistance.MasterDao
import us.kostenko.architecturecomponentstmdb.master.repository.webservice.MoviesWebService
import java.util.Date

interface MoviesRepository {
    fun getMovies(): LiveData<PagedList<MovieItem>>
}

class MoviesRepositoryImpl(private val webService: MoviesWebService,
                       private val masterDao: MasterDao): MoviesRepository {

    private lateinit var pagedList: LiveData<PagedList<MovieItem>>

    override fun getMovies(): LiveData<PagedList<MovieItem>> {
        val config = PagedList.Config.Builder()
                        .setPageSize(PAGE_SIZE)
                        .setEnablePlaceholders(true).build()
        pagedList = LivePagedListBuilder<Int, MovieItem>(masterDao.getMovies(), config)
                .setBoundaryCallback(movieBoundaryCallback()).build()
        return pagedList
    }

    private fun movieBoundaryCallback() = object: PagedList.BoundaryCallback<MovieItem>() {

        private var lastRequestedPage = 1
        private var sortOrder = 1
        private var isLoading = false

        override fun onItemAtEndLoaded(itemAtEnd: MovieItem) = requestAndSaveTheData()

        override fun onZeroItemsLoaded() = requestAndSaveTheData()

        private fun requestAndSaveTheData() {
            launchIfNotLoading {
                val movies = webService.getMovies(lastRequestedPage).await().results
                movies.map { it.dateUpdate = Date(); it.sort = sortOrder++ }
                Timber.d("movies from Gson: $movies")
//                masterDao.saveMovies(movies)
                masterDao.saveMovies(movies)
                lastRequestedPage++
            }
        }

        private fun launchIfNotLoading(callback: suspend CoroutineScope.() -> Unit) {
            if (isLoading) return
            GlobalScope.launch {
                isLoading = true
                callback()
                isLoading = false
            }
        }
    }

    companion object {
        const val PAGE_SIZE = 20
    }
}