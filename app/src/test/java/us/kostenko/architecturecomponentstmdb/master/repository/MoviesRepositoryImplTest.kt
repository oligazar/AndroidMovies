package us.kostenko.architecturecomponentstmdb.master.repository

import org.junit.Test

class MoviesRepositoryImplTest {

    @Test
    fun getInProgress() {
    }

    @Test
    fun getMovies() {
    }
}

//@RunWith(Parameterized::class)
//class InMemoryRepositoryTest(type : MoviesRepository.Type) {
//    companion object {
//        @JvmStatic
//        @Parameterized.Parameters(name = "{0}")
//        fun params() = listOf(IN_MEMORY_BY_ITEM, IN_MEMORY_BY_PAGE)
//    }
//
//    @Suppress("unused")
//    @get:Rule // used to make all live data calls sync
//    val instantExecutor = InstantTaskExecutorRule()
//    private val fakeApi: MoviesWebApi = MoviesWebApi()
//    private val networkExecutor = Executor { command -> command.run() }
//    private val repository: MoviesRepository = when(type) {
//        IN_MEMORY_BY_PAGE -> InMemoryByPageKeyRepository(
//                redditApi = fakeApi,
//                networkExecutor = networkExecutor)
//        IN_MEMORY_BY_ITEM -> InMemoryByItemRepository(
//                redditApi = fakeApi,
//                networkExecutor = networkExecutor)
//        else -> throw IllegalArgumentException()
//    }
//    private val postFactory = PostFactory()
//    /**
//     * asserts that empty list works fine
//     */
//    @Test
//    fun `movies, when source empty, should return empty pagedList`() {
//        val listing = repository.movies
//        val pagedList = getPagedList(listing)
//        MatcherAssert.assertThat(pagedList.size, CoreMatchers.`is`(0))
//    }
//
//    /**
//     * asserts that a list w/ single item is loaded properly
//     */
//    @Test
//    fun oneItem() {
//        val post = postFactory.createRedditPost("foo")
//        fakeApi.addPost(post)
//        val listing = repository.movies
//        MatcherAssert.assertThat(getPagedList(listing), `is`(listOf(post)))
//    }
//
//    /**
//     * asserts loading a full list in multiple pages
//     */
//    @Test
//    fun verifyCompleteList() {
//        val posts = (0..10).map { postFactory.createRedditPost("bar") }
//        posts.forEach(fakeApi::addPost)
//        val listing = repository.movies
//        // trigger loading of the whole list
//        val pagedList = getPagedList(listing)
//        pagedList.loadAround(posts.size - 1)
//        MatcherAssert.assertThat(pagedList, CoreMatchers.`is`(posts))
//    }
//
//    /**
//     * asserts the failure message when the initial load cannot complete
//     */
//    @Test
//    fun failToLoadInitial() {
//        fakeApi.failureMsg = "xxx"
//        val listing = repository.movies
//        // trigger load
//        getPagedList(listing)
//        MatcherAssert.assertThat(getNetworkState(listing), CoreMatchers.`is`(NetworkState.error("xxx")))
//    }
//
//    /**
//     * asserts the retry logic when initial load request fails
//     */
//    @Test
//    fun retryInInitialLoad() {
//        fakeApi.addPost(postFactory.createRedditPost("foo"))
//        fakeApi.failureMsg = "xxx"
//        val listing = repository.movies
//        // trigger load
//        val pagedList = getPagedList(listing)
//        MatcherAssert.assertThat(pagedList.size, CoreMatchers.`is`(0))
//
//        @Suppress("UNCHECKED_CAST")
//        val networkObserver = Mockito.mock(Observer::class.java) as Observer<NetworkState>
//        listing.networkState.observeForever(networkObserver)
//        fakeApi.failureMsg = null
//        listing.retry()
//        MatcherAssert.assertThat(pagedList.size, CoreMatchers.`is`(1))
//        MatcherAssert.assertThat(getNetworkState(listing), CoreMatchers.`is`(NetworkState.LOADED))
//        val inOrder = Mockito.inOrder(networkObserver)
//        inOrder.verify(networkObserver).onChanged(NetworkState.error("xxx"))
//        inOrder.verify(networkObserver).onChanged(NetworkState.LOADING)
//        inOrder.verify(networkObserver).onChanged(NetworkState.LOADED)
//        inOrder.verifyNoMoreInteractions()
//    }
//
//    /**
//     * asserts the retry logic when initial load succeeds but subsequent loads fails
//     */
//    @Test
//    fun retryAfterInitialFails() {
//        val posts = (0..10).map { postFactory.createRedditPost("bar") }
//        posts.forEach(fakeApi::addPost)
//        val listing = repository.movies
//        val list = getPagedList(listing)
//        MatcherAssert.assertThat("test sanity, we should not load everything",
//                                 list.size < posts.size, CoreMatchers.`is`(true))
//        MatcherAssert.assertThat(getNetworkState(listing), CoreMatchers.`is`(NetworkState.LOADED))
//        fakeApi.failureMsg = "fail"
//        list.loadAround(posts.size - 1)
//        MatcherAssert.assertThat(getNetworkState(listing), CoreMatchers.`is`(NetworkState.error("fail")))
//        fakeApi.failureMsg = null
//        listing.retry()
//        MatcherAssert.assertThat(getNetworkState(listing), CoreMatchers.`is`(NetworkState.LOADED))
//        MatcherAssert.assertThat(list, CoreMatchers.`is`(posts))
//    }
//
//    /**
//     * asserts refresh loads the new data
//     */
//    @Test
//    fun refresh() {
//        val postsV1 = (0..5).map { postFactory.createRedditPost("bar") }
//        postsV1.forEach(fakeApi::addPost)
//        val listing = repository.postsOfSubreddit(subReddit = "bar", pageSize = 5)
//        val list = getPagedList(listing)
//        list.loadAround(10)
//        val postsV2 = (0..10).map { postFactory.createRedditPost("bar") }
//        fakeApi.clear()
//        postsV2.forEach(fakeApi::addPost)
//
//        @Suppress("UNCHECKED_CAST")
//        val refreshObserver = Mockito.mock(Observer::class.java) as Observer<NetworkState>
//        listing.refreshState.observeForever(refreshObserver)
//        listing.refresh()
//
//        val list2 = getPagedList(listing)
//        list2.loadAround(10)
//        MatcherAssert.assertThat(list2, CoreMatchers.`is`(postsV2))
//        val inOrder = Mockito.inOrder(refreshObserver)
//        inOrder.verify(refreshObserver).onChanged(NetworkState.LOADED) // initial state
//        inOrder.verify(refreshObserver).onChanged(NetworkState.LOADING)
//        inOrder.verify(refreshObserver).onChanged(NetworkState.LOADED)
//    }
//
//    /**
//     * asserts that refresh also works after failure
//     */
//    @Test
//    fun refreshAfterFailure() {
//        val posts = (0..5).map { postFactory.createRedditPost("bar") }
//        posts.forEach(fakeApi::addPost)
//
//        fakeApi.failureMsg = "xx"
//        val listing = repository.postsOfSubreddit(subReddit = "bar", pageSize = 5)
//        getPagedList(listing)
//        MatcherAssert.assertThat(getNetworkState(listing), CoreMatchers.`is`(NetworkState.error("xx")))
//        fakeApi.failureMsg = null
//        listing.refresh()
//        // get the new list since refresh will create a new paged list
//        MatcherAssert.assertThat(getPagedList(listing), CoreMatchers.`is`(posts))
//    }
//
//    /**
//     * extract the latest paged list from the listing
//     */
//    private fun getPagedList(movies: LiveData<PagedList<MovieItem>>): PagedList<MovieItem> {
//        val observer = LoggingObserver<PagedList<MovieItem>>()
//        movies.observeForever(observer)
//        MatcherAssert.assertThat(observer.value, CoreMatchers.`is`(CoreMatchers.notNullValue()))
//        return observer.value!!
//    }
//
//    /**
//     * extract the latest network state from the listing
//     */
//    private fun getNetworkState(listing: Listing<RedditPost>) : NetworkState? {
//        val networkObserver = LoggingObserver<NetworkState>()
//        listing.networkState.observeForever(networkObserver)
//        return networkObserver.value
//    }
//
//    /**
//     * simple observer that logs the latest value it receives
//     */
//    private class LoggingObserver<T> : Observer<T> {
//        var value : T? = null
//        override fun onChanged(t: T?) {
//            this.value = t
//        }
//    }
//}