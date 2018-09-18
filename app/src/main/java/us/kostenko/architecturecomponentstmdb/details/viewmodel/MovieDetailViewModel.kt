package us.kostenko.architecturecomponentstmdb.details.viewmodel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import us.kostenko.architecturecomponentstmdb.common.di.Injector
import us.kostenko.architecturecomponentstmdb.details.model.Movie
import us.kostenko.architecturecomponentstmdb.details.repository.MovieDetailRepository

class MovieDetailViewModel(application: Application): AndroidViewModel(application) {

    private var movieId: Int = 0
    val movie: LiveData<Movie> by lazy {
        if (movieId == 0) throw IllegalAccessException("The movieId shouldn't be '0'")
        repo.getMovie(movieId)
    }

    private val repo: MovieDetailRepository by lazy { Injector.provideMovieDetailRepository(application) }

    fun init(movieId: Int) {
        this.movieId = movieId
    }
}