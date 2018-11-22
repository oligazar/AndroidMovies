package us.kostenko.architecturecomponentstmdb.master.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.paging.PagedList
import us.kostenko.architecturecomponentstmdb.master.model.MovieItem
import us.kostenko.architecturecomponentstmdb.master.repository.MoviesRepository
import us.kostenko.architecturecomponentstmdb.testing.OpenForTesting

@OpenForTesting
class MoviesViewModel(private val repo: MoviesRepository): ViewModel() {

    val movies: LiveData<PagedList<MovieItem>> by lazy { repo.getMovies() }
}