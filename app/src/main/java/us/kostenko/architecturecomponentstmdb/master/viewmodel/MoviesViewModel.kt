package us.kostenko.architecturecomponentstmdb.master.viewmodel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import us.kostenko.architecturecomponentstmdb.common.di.Injector
import us.kostenko.architecturecomponentstmdb.master.repository.MoviesRepository

class MoviesViewModel(application: Application) : AndroidViewModel(application) {

//    val movies: ArrayList<Movie> by lazy { repo.getMovies() }
    suspend fun getMovies() = repo.getMovies()
//    val movies: LiveData<ArrayList<Movie>> by lazy { repo.getMovies() }

    private val repo: MoviesRepository by lazy { Injector.provideMoviesRepository(application) }
}