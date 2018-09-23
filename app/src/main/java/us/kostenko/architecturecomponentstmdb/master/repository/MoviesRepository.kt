package us.kostenko.architecturecomponentstmdb.master.repository

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.paging.PageKeyedDataSource
import android.arch.paging.PagedList
import kotlinx.coroutines.experimental.Dispatchers
import kotlinx.coroutines.experimental.GlobalScope
import kotlinx.coroutines.experimental.android.Main
import kotlinx.coroutines.experimental.launch
import us.kostenko.architecturecomponentstmdb.details.model.Movie
import us.kostenko.architecturecomponentstmdb.master.repository.webservice.MoviesWebService

class MoviesRepository(private val webService: MoviesWebService) {

//    fun getMovies(): LiveData<ArrayList<Movie>> {
//        val ldMovies = MutableLiveData<ArrayList<Movie>>()
//        GlobalScope.launch {
//            val movies = webService.getMovies().await()
//            ldMovies.postValue(movies.results)
//        }
//        return ldMovies
//    }

    fun getMovies(): LiveData<PagedList<Movie>> {
        val movieData = MutableLiveData<PagedList<Movie>>()
        val config = PagedList.Config.Builder().setPageSize(10).setEnablePlaceholders(false).build()
        val dataSource = MoviesDataSource()
        val list = PagedList.Builder(dataSource, config)
                .setFetchExecutor { it.run() }
                .setNotifyExecutor { GlobalScope.launch(Dispatchers.Main) { it.run() }}.build()
        movieData.value = list
        return movieData
    }

//        val factory =
//        val liveDataPl = LivePagedListBuilder<Int, Movie>(factory, config).build()

    inner class MoviesDataSource: PageKeyedDataSource<Int, Movie>() {

        override fun loadInitial(params: LoadInitialParams<Int>,
                                         callback: LoadInitialCallback<Int, Movie>) {
            GlobalScope.launch {
                val movies = webService.getMovies(1).await().results
                callback.onResult(movies, null, 2)
            }
        }

        override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, Movie>) {
            GlobalScope.launch {
                val movies = webService.getMovies(params.key).await().results
                callback.onResult(movies, params.key + 1)
            }
        }

        override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, Movie>) { }
    }
}