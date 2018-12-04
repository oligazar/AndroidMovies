package us.kostenko.architecturecomponentstmdb.master.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import kotlinx.coroutines.CoroutineScope
import timber.log.Timber
import us.kostenko.architecturecomponentstmdb.common.Coroutines
import us.kostenko.architecturecomponentstmdb.details.model.Movie
import us.kostenko.architecturecomponentstmdb.master.model.MovieItem
import us.kostenko.architecturecomponentstmdb.master.repository.persistance.MasterDao
import us.kostenko.architecturecomponentstmdb.master.repository.webservice.MoviesWebApi
import us.kostenko.architecturecomponentstmdb.master.view.adapter.RecyclerState

interface MoviesRepository {

    fun movies(pageSize: Int = PAGE_SIZE): Listing<MovieItem>

    enum class Type {
        IN_MEMORY_BY_ITEM,
        IN_MEMORY_BY_PAGE,
        DB
    }
}

data class Listing<T> (
        val     pagedList: LiveData<PagedList<T>>,
        val networkState: LiveData<RecyclerState>,
        val refreshState: LiveData<RecyclerState>,
        val refresh: () -> Unit,
        val retry: () -> Unit
)

const val PAGE_SIZE = 20

class MoviesRepositoryImpl(private val moviesApi: MoviesWebApi,
                           private val moviesDao: MasterDao,
                           private val coroutines: Coroutines): MoviesRepository {


    private val networkState = MutableLiveData<RecyclerState>()
    private val refreshState = MutableLiveData<RecyclerState>()

    override fun movies(pageSize: Int): Listing<MovieItem> {
        val config = PagedList.Config.Builder()
                .setPageSize(pageSize)
                .setEnablePlaceholders(true).build()
        val builder = LivePagedListBuilder<Int, MovieItem>(moviesDao.getMovies(), config)
                .setBoundaryCallback(movieBoundaryCallback())
        return Listing(builder.build(),
                       networkState,
                       refreshState,
                       ::refresh,
                       ::retry)
    }

    private var isLoading = false
    private var lastRequestedPage = 1

    /**
     * When refresh is called, we simply run a fresh network request and when it arrives, clear
     * the database table and insert all new items in a transaction.
     * <p>
     * Since the PagedList already uses a database bound data source, it will automatically be
     * updated after the database transaction is finished.
     */
    private fun refresh() {
        lastRequestedPage = 1

        fetchMovies(true, moviesDao::clearAndSaveNew)
    }

    private fun retry() = fetchMovies(false, ::saveMovies)

    private fun fetchMovies(isRefreshing: Boolean = false, handle: (ArrayList<Movie>) -> Unit) {
        launchIfNotLoading {
            try {
                updateState(isRefreshing, RecyclerState.InProgress)
                val movies = moviesApi.getMovies(lastRequestedPage).await().results
                handle(movies)

                updateState(isRefreshing, RecyclerState.Loaded)
                lastRequestedPage++
            } catch (e: Exception) {
                Timber.e(e)
                updateState(isRefreshing, RecyclerState.Failed(e.message))
            }
        }
    }

    private fun updateState(isRefreshing: Boolean, state: RecyclerState) {
        if (isRefreshing) {
            refreshState
        } else {
            networkState
        }.apply {
            postValue(state)
        }
    }

    private fun launchIfNotLoading(callback: suspend CoroutineScope.() -> Unit) {
        if (isLoading) return
        coroutines {
            isLoading = true
            callback()
            isLoading = false
        }
    }

    private fun saveMovies(movies: ArrayList<Movie>) {
        Timber.d("movies from Gson: $movies")
        moviesDao.saveMovies(movies)
    }

    private fun movieBoundaryCallback() = object: PagedList.BoundaryCallback<MovieItem>() {

        override fun onZeroItemsLoaded() = fetchMovies(false, ::saveMovies)

        override fun onItemAtEndLoaded(itemAtEnd: MovieItem) = fetchMovies(false, ::saveMovies)
    }
}
