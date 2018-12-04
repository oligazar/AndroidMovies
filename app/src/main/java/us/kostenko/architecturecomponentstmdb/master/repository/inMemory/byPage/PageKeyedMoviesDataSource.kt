package us.kostenko.architecturecomponentstmdb.master.repository.inMemory.byPage

import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import us.kostenko.architecturecomponentstmdb.common.Coroutines
import us.kostenko.architecturecomponentstmdb.details.model.toMovieItem
import us.kostenko.architecturecomponentstmdb.master.model.MovieItem
import us.kostenko.architecturecomponentstmdb.master.repository.webservice.MoviesWebApi
import us.kostenko.architecturecomponentstmdb.master.view.adapter.RecyclerState
import java.io.IOException

class PageKeyedMoviesDataSource(private val moviesApi: MoviesWebApi,
                                private val coroutines: Coroutines): PageKeyedDataSource<Int, MovieItem>() {

    private var page = 1
    val initialLoad = MutableLiveData<RecyclerState>()
    val networkState = MutableLiveData<RecyclerState>()
    private var retry: (() -> Any)? = null

    override fun loadInitial(params: LoadInitialParams<Int>,
                             callback: LoadInitialCallback<Int, MovieItem>) {
        fetchMovies(true, { loadInitial(params, callback) }) { movieItems ->
            callback.onResult(movieItems, null, page)
        }
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, MovieItem>) {
        networkState.postValue(RecyclerState.InProgress)

        fetchMovies(false, { loadAfter(params, callback) }) { movieItems ->
            callback.onResult(movieItems, page)
        }
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, MovieItem>) { /*NoOp*/ }

    fun retryAllFailed() {
        val prevRetry = retry
        retry = null
        prevRetry?.let {
            coroutines {
                it.invoke()
            }
        }
    }

    private fun fetchMovies(isInitial: Boolean, retryCallback: () -> Unit, handle: (List<MovieItem>) -> Unit) {

        setState(RecyclerState.InProgress, isInitial)

        // triggered by a refresh, we better execute sync
        coroutines {
            try {
                val movies = moviesApi.getMovies(page).await()
                val movieItems = movies.results.map { it.toMovieItem() }
                retry = null
                page++
                handle(movieItems)
                setState(RecyclerState.Loaded, isInitial)

            } catch (ioException: IOException) {
                retryCallback()
                val error = RecyclerState.Failed(
                        ioException.message ?: "unknown error")
                setState(error, isInitial)
            }
        }
    }

    private fun setState(state: RecyclerState, isInitial: Boolean) {
        networkState.postValue(state)
        if (isInitial) initialLoad.postValue(state)
    }
}