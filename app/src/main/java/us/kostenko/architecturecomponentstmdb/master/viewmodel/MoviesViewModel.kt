package us.kostenko.architecturecomponentstmdb.master.viewmodel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.paging.PagedList
import us.kostenko.architecturecomponentstmdb.common.di.Injector
import us.kostenko.architecturecomponentstmdb.master.model.MovieItem
import us.kostenko.architecturecomponentstmdb.master.repository.MoviesRepository

class MoviesViewModel(application: Application) : AndroidViewModel(application) {

    private val repo: MoviesRepository by lazy { Injector.provideMoviesRepository(application) }

    val movies: LiveData<PagedList<MovieItem>> by lazy { repo.getMovies() }
}