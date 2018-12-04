package us.kostenko.architecturecomponentstmdb.master.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import androidx.paging.PagedList
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import org.assertj.core.api.Assertions.assertThat
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito
import us.kostenko.architecturecomponentstmdb.common.TestCoroutines
import us.kostenko.architecturecomponentstmdb.details.model.Movie
import us.kostenko.architecturecomponentstmdb.details.model.toMovieItem
import us.kostenko.architecturecomponentstmdb.master.model.Dates
import us.kostenko.architecturecomponentstmdb.master.model.MovieItem
import us.kostenko.architecturecomponentstmdb.master.model.Movies
import us.kostenko.architecturecomponentstmdb.master.repository.inMemory.byPage.InMemoryByPageKeyRepository
import us.kostenko.architecturecomponentstmdb.master.repository.webservice.MoviesWebApi
import us.kostenko.architecturecomponentstmdb.master.view.adapter.RecyclerState
import java.util.Date
import java.util.concurrent.atomic.AtomicInteger
import kotlin.math.ceil

class MoviesRepositoryImplTest {

    @Test
    fun getInProgress() {
    }

    @Test
    fun getMovies() {
    }
}

class MovieFactory {
    private val counter = AtomicInteger(0)

    fun createMovie(liked: Boolean = false,
                    sort: Int = 1,
                    prefix: String = "",
                    dateUpdate: Date = Date()): Movie {
        val id = counter.incrementAndGet()
        return Movie(id, "${prefix}Title", "${prefix}Date", "${prefix}poster", "${prefix}backdrop",
                     "${prefix}overview", "${prefix}eng", "${prefix}origTitle",
                     null, dateUpdate).apply {
            this.liked = liked
            this.sort = sort
        }
    }
}

class InMemoryRepositoryTest {

    @Suppress("unused")
    @get:Rule // used to make all live data calls sync
    val instantExecutor = InstantTaskExecutorRule()
    private val fakeApi = FakeMoviesWebApi(pageSize = 20)
    private val coroutines = TestCoroutines()
    private val repository: MoviesRepository = InMemoryByPageKeyRepository(
                moviesApi = fakeApi,
                coroutines = coroutines)

    private val movieFactory = MovieFactory()
    /**
     * asserts that empty list works fine
     */
    @Test
    fun `movies, when source empty, should return empty pagedList`() {
        val listing = repository.movies()
        val pagedList = getPagedList(listing)
        assertThat(pagedList.size).isEqualTo(0)
    }

    /**
     * asserts that a list w/ single item is loaded properly
     */
    @Test
    fun oneItem() {
        val post = movieFactory.createMovie()
        fakeApi.addMovie(post)
        val listing = repository.movies()
        assertThat(getPagedList(listing)).isEqualTo(listOf(post.toMovieItem()))
    }

    /**
     * asserts loading a full list in multiple pages
     */
    @Test
    fun verifyCompleteList() {
        val movies = (0..10).map { movieFactory.createMovie() }
        movies.forEach(fakeApi::addMovie)
        val listing = repository.movies()
        // trigger loading of the whole list
        val pagedList = getPagedList(listing)
        pagedList.loadAround(movies.size - 1)
        assertThat(pagedList).isEqualTo(movies.map { it.toMovieItem() })
    }

    /**
     * asserts the failure message when the initial load cannot complete
     */
    @Test
    fun failToLoadInitial() {
        fakeApi.failureMsg = "xxx"
        val listing = repository.movies()
        // trigger load
        getPagedList(listing)
        assertThat(getNetworkState(listing)).isEqualTo(RecyclerState.Failed("xxx"))
    }

    /**
     * asserts the retry logic when initial load request fails
     */
    @Test
    fun retryInInitialLoad() {
        fakeApi.addMovie(movieFactory.createMovie(prefix = "foo"))
        fakeApi.failureMsg = "xxx"
        val listing = repository.movies()
        // trigger load
        val pagedList = getPagedList(listing)
        assertThat(pagedList.size).isEqualTo(0)

        @Suppress("UNCHECKED_CAST")
        val networkObserver = Mockito.mock(Observer::class.java) as Observer<RecyclerState>
        listing.networkState.observeForever(networkObserver)
        fakeApi.failureMsg = null
        listing.retry()
        assertThat(pagedList.size).isEqualTo(1)
        assertThat(getNetworkState(listing)).isEqualTo(RecyclerState.Loaded)
        val inOrder = Mockito.inOrder(networkObserver)
        inOrder.verify(networkObserver).onChanged(RecyclerState.Failed("xxx"))
        inOrder.verify(networkObserver).onChanged(RecyclerState.InProgress)
        inOrder.verify(networkObserver).onChanged(RecyclerState.Loaded)
        inOrder.verifyNoMoreInteractions()
    }

    /**
     * asserts the retry logic when initial load succeeds but subsequent loads fails
     */
    @Test
    fun retryAfterInitialFails() {
        val posts = (0..10).map { movieFactory.createMovie(prefix = "bar") }
        posts.forEach(fakeApi::addMovie)
        val listing = repository.movies()
        val list = getPagedList(listing)
        assertThat(list.size < posts.size).isEqualTo(true).describedAs("\"test sanity, we should not load everything\"")
        assertThat(getNetworkState(listing)).isEqualTo(RecyclerState.Loaded)
        fakeApi.failureMsg = "fail"
        list.loadAround(posts.size - 1)
        assertThat(getNetworkState(listing)).isEqualTo(RecyclerState.Failed("fail"))
        fakeApi.failureMsg = null
        listing.retry()
        assertThat(getNetworkState(listing)).isEqualTo(RecyclerState.Loaded)
        assertThat(list).isEqualTo(posts)
    }

    /**
     * asserts refresh loads the new data
     */
    @Test
    fun refresh() {
        val postsV1 = (0..5).map { movieFactory.createMovie(prefix = "bar") }
        postsV1.forEach(fakeApi::addMovie)
        val listing = repository.movies(pageSize = 5)
        val list = getPagedList(listing)
        list.loadAround(10)
        val postsV2 = (0..10).map { movieFactory.createMovie(prefix = "bar") }
        fakeApi.clear()
        postsV2.forEach(fakeApi::addMovie)

        @Suppress("UNCHECKED_CAST")
        val refreshObserver = Mockito.mock(Observer::class.java) as Observer<RecyclerState>
        listing.refreshState.observeForever(refreshObserver)
        listing.refresh()

        val list2 = getPagedList(listing)
        list2.loadAround(10)
        assertThat(list2).isEqualTo(postsV2)
        val inOrder = Mockito.inOrder(refreshObserver)
        inOrder.verify(refreshObserver).onChanged(RecyclerState.Loaded) // initial state
        inOrder.verify(refreshObserver).onChanged(RecyclerState.InProgress)
        inOrder.verify(refreshObserver).onChanged(RecyclerState.Loaded)
    }

    /**
     * asserts that refresh also works after failure
     */
    @Test
    fun refreshAfterFailure() {
        val posts = (0..5).map { movieFactory.createMovie(prefix = "bar") }
        posts.forEach(fakeApi::addMovie)

        fakeApi.failureMsg = "xx"
        val listing = repository.movies(pageSize = 5)
        getPagedList(listing)
        assertThat(getNetworkState(listing)).isEqualTo(RecyclerState.Failed("xx"))
        fakeApi.failureMsg = null
        listing.refresh()
        // get the new list since refresh will create a new paged list
        assertThat(getPagedList(listing)).isEqualTo(posts)
    }

    /**
     * extract the latest paged list from the listing
     */
    private fun getPagedList(movies: Listing<MovieItem>): PagedList<MovieItem> {
        val observer = LoggingObserver<PagedList<MovieItem>>()
        movies.pagedList.observeForever(observer)
        assertThat(observer.value).isNotNull
        return observer.value!!
    }

    /**
     * extract the latest network state from the listing
     */
    private fun getNetworkState(listing: Listing<MovieItem>) : RecyclerState {
        val networkObserver = LoggingObserver<RecyclerState>()
        listing.networkState.observeForever(networkObserver)
        return networkObserver.value!!
    }

    /**
     * simple observer that logs the latest value it receives
     */
    private class LoggingObserver<T> : Observer<T> {
        var value : T? = null
        override fun onChanged(t: T?) {
            this.value = t
        }
    }
}

class FakeMoviesWebApi(private val pageSize: Int): MoviesWebApi {

    private val movies = mutableListOf<Movie>()

    var failureMsg: String? = null

    override fun getMovies(page: Int, region: String?, apiKey: String, language: String): Deferred<Movies> {
        val toIndex = page * pageSize - 1
        val fromIndex = toIndex - pageSize + 1
        val items = movies.subList(fromIndex, toIndex).toArrayList()
        val dates = Dates("max", "min")
        val totalPages = ceil(movies.size.toFloat() / pageSize).toInt()
        return GlobalScope.async { Movies(items, page, movies.size, dates, totalPages) }

    }

    fun clear() {
        movies.clear()
    }

    inline fun <reified T> Collection<T>.toArrayList()= ArrayList(this)

    fun addMovie(movie: Movie) {
        movies.add(movie)
    }
}
