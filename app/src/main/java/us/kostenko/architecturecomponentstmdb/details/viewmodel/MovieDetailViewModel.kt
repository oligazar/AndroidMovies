package us.kostenko.architecturecomponentstmdb.details.viewmodel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import us.kostenko.architecturecomponentstmdb.common.di.Injector
import us.kostenko.architecturecomponentstmdb.details.model.Movie
import us.kostenko.architecturecomponentstmdb.details.repository.MovieDetailRepository



class MovieDetailViewModel(application: Application, private var movieId: Int = 0): AndroidViewModel(application) {

    private val repo: MovieDetailRepository by lazy { Injector.provideMovieDetailRepository(application) }

    val movie: LiveData<Movie> by lazy { repo.getMovie(movieId) }

    fun like(id: Int, like: Boolean) {
        repo.like(id, like)
    }
}