package us.kostenko.architecturecomponentstmdb.master.viewmodel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import us.kostenko.architecturecomponentstmdb.common.di.Injector
import us.kostenko.architecturecomponentstmdb.details.model.Movie
import us.kostenko.architecturecomponentstmdb.master.repository.MoviesRepository
import java.util.*

class MoviesViewModel(application: Application) : AndroidViewModel(application) {

    val movies: ArrayList<Movie> by lazy { repo.getMovies() }
//    val movies: LiveData<ArrayList<Movie>> by lazy { repo.getMovies() }

    private val repo: MoviesRepository by lazy { Injector.provideMoviesRepository(application) }
}