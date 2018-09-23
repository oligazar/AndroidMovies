package us.kostenko.architecturecomponentstmdb.master.viewmodel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.paging.PagedList
import us.kostenko.architecturecomponentstmdb.common.di.Injector
import us.kostenko.architecturecomponentstmdb.details.model.Movie
import us.kostenko.architecturecomponentstmdb.master.repository.MoviesRepository

class MoviesViewModel(application: Application) : AndroidViewModel(application) {

//    val movies: ArrayList<Movie> by lazy { repo.getMovies() }
//    suspend fun getMovies() = repo.getMovies()
    val movies: LiveData<PagedList<Movie>> by lazy { repo.getMovies() }

    private val repo: MoviesRepository by lazy { Injector.provideMoviesRepository(application) }
}