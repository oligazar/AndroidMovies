package us.kostenko.architecturecomponentstmdb.master.repository.inMemory.byPage

import androidx.lifecycle.Transformations
import androidx.paging.LivePagedListBuilder
import us.kostenko.architecturecomponentstmdb.common.Coroutines
import us.kostenko.architecturecomponentstmdb.master.model.MovieItem
import us.kostenko.architecturecomponentstmdb.master.repository.Listing
import us.kostenko.architecturecomponentstmdb.master.repository.MoviesRepository
import us.kostenko.architecturecomponentstmdb.master.repository.webservice.MoviesWebApi

/**
 * Repository implementation that returns a Listing that loads data directly from network by using
 * the previous / next page keys returned in the query.
 */
class InMemoryByPageKeyRepository(private val moviesApi: MoviesWebApi,
                                  private val coroutines: Coroutines): MoviesRepository {

    override fun movies(pageSize: Int): Listing<MovieItem> {
        val sourceFactory = MoviesDataSourceFactory<Int, MovieItem, PageKeyedMoviesDataSource> {
            PageKeyedMoviesDataSource(moviesApi, coroutines)
        }

        val livePagedList = LivePagedListBuilder(sourceFactory, pageSize)
                // provide custom executor for network requests, otherwise it will default to
                // Arch Components' IO pool which is also used for disk access
//                .setFetchExecutor(networkExecutor)
                .build()
        val refreshState = Transformations.switchMap(sourceFactory.sourceLiveData) {
            it.initialLoad
        }
        return Listing(pagedList = livePagedList,
                       networkState = Transformations.switchMap(sourceFactory.sourceLiveData) {
                           it.networkState
                       },
                       refreshState = refreshState,
                       refresh = {
                           sourceFactory.sourceLiveData.value?.retryAllFailed()
                       },
                       retry = {
                           sourceFactory.sourceLiveData.value?.invalidate()
                       })
    }
}

