package us.kostenko.architecturecomponentstmdb.master.viewmodel

import androidx.lifecycle.ViewModel
import us.kostenko.architecturecomponentstmdb.master.model.MovieItem
import us.kostenko.architecturecomponentstmdb.master.repository.Listing
import us.kostenko.architecturecomponentstmdb.master.repository.MoviesRepository
import us.kostenko.architecturecomponentstmdb.testing.OpenForTesting

@OpenForTesting
class MoviesViewModel(private val repo: MoviesRepository): ViewModel() {

    private val repoListing: Listing<MovieItem> by lazy { repo.movies() }

    val movies = repoListing.pagedList
    val refreshState = repoListing.refreshState
    val networkState = repoListing.networkState

    fun refresh() {
        repoListing.refresh()
    }

    fun retry() {
        repoListing.retry()
    }
}