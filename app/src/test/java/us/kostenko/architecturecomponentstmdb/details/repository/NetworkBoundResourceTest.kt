package us.kostenko.architecturecomponentstmdb.details.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.nhaarman.mockitokotlin2.times
import okhttp3.MediaType
import okhttp3.ResponseBody
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import us.kostenko.architecturecomponentstmdb.common.Coroutines
import us.kostenko.architecturecomponentstmdb.common.TestCoroutines
import us.kostenko.architecturecomponentstmdb.details.viewmodel.netres.BoundResourceAdapter
import us.kostenko.architecturecomponentstmdb.details.viewmodel.netres.NetworkBoundResource
import us.kostenko.architecturecomponentstmdb.details.viewmodel.netres.State
import us.kostenko.architecturecomponentstmdb.utils.mock
import java.util.concurrent.atomic.AtomicReference
import kotlin.properties.Delegates.observable

class NetworkBoundResourceTest {

    // To avoid RuntimeException: Method getMainLooper in android.os.Looper not mocked
    @get:Rule val instantExecutorRule = InstantTaskExecutorRule()

    private val dbData = MutableLiveData<Foo>()
    private var handleShouldFetch: (Foo?) -> Boolean = { it == null || it.value <= 0 }
    private var handleSaveCallResult: (Foo) -> Unit = { foo ->
        saved.set(foo)
        dbData.value = foo
    }

    private lateinit var internetIterator: Iterator<Foo?>
    private var internetValues by observable<Sequence<Foo?>>(emptySequence()) { _, _, new -> internetIterator = new.iterator() }

    private val saved = AtomicReference<Foo>()
    private val adapter: BoundResourceAdapter<Foo, State<Foo>> = mock()

    private val coroutines: Coroutines = TestCoroutines()
    private val networkBoundResource: NetworkBoundResource<Foo, Foo, State<Foo>> by lazy { createNetworkBoundResource(coroutines, adapter) }

    @Before
    fun init() {
        val observer: Observer<State<Foo>> = mock()
        networkBoundResource.asLiveData().observeForever(observer)
        saved.set(null)
    }

    @Test
    fun `reload saves value to database`() {
        // arrange
        dbData.value = null
        internetValues = sequenceOf(
                FOO_1)

        // act
        networkBoundResource.reload()

        // assert
        assertThat(saved.get(), `is`(
                FOO_1))
    }

    @Test
    fun `db empty success from network`() {
        dbData.value = null
        internetValues = sequenceOf(
                FOO_1)

        networkBoundResource.reload()

        adapter.inOrder {
            verify().onProgress()
            verify().onData(
                    FOO_1)
        }
    }

    @Test
    fun `db not empty`() {
        dbData.value = FOO_1

        networkBoundResource.reload()

        adapter.inOrder {
            verify().onProgress()
            verify().onData(
                    FOO_1)
        }
    }

    @Test
    fun `db not empty reload`() {
        dbData.value = FOO_RELOAD
        internetValues = sequenceOf(
                FOO_1)

        networkBoundResource.reload()

        adapter.inOrder {
            verify().onProgress()
            verify().onData(
                    FOO_1)
        }
    }

    @Test
    fun `db empty success and failure from network`() {
        dbData.value = null
        internetValues =  sequence {
            yield(FOO_RELOAD)
            yield(FOO_EXCEPTION)
        }

        networkBoundResource.reload()
        networkBoundResource.reload()

        adapter.inOrder {
            verify().onProgress()
            verify().onData(
                    FOO_RELOAD)
            verify().onProgress()
            verify().onError(
                    EXCEPTION)
        }
    }

    @Test
    fun `db empty failure and success from network`() {
        dbData.value = null
        internetValues = sequence {
            yield(FOO_EXCEPTION)
            yield(FOO_1)
        }

        networkBoundResource.reload()
        networkBoundResource.reload()

        adapter.inOrder {
            verify().onProgress()
            verify().onError(
                    EXCEPTION)
            verify().onProgress()
            verify().onData(
                    FOO_1)
        }
    }

    @Test
    fun `db empty, network error, db update`() {

        dbData.value = null
        internetValues = sequenceOf(
                FOO_EXCEPTION)

        networkBoundResource.reload()
        dbData.value = FOO_2

        adapter.inOrder {
            verify().onProgress()
            verify(adapter, times(2)).onError(
                    EXCEPTION)
        }
    }

    private fun createNetworkBoundResource(coroutines: Coroutines, adapter: BoundResourceAdapter<Foo, State<Foo>>) =
            object : NetworkBoundResource<Foo, Foo, State<Foo>>(coroutines, adapter) {

        override fun saveResult(item: Foo) = handleSaveCallResult(item)

        override fun shouldFetch(data: Foo?) = handleShouldFetch(data)

        override fun loadFromDb(): LiveData<Foo> = dbData

        override suspend fun fetchData(): Foo? {
            val nextFoo = internetIterator.next()
            return if (nextFoo != FOO_EXCEPTION) nextFoo else throw EXCEPTION
        }
    }

    companion object {
        private const val ERROR_MESSAGE = "errorMessage"
        private val BODY = ResponseBody.create(MediaType.parse("application/json"), "{\"status_code\":34,\"status_message\":\"$ERROR_MESSAGE\"}")
        private val EXCEPTION = HttpException(Response.error<Foo>(400,
                                                                                                                            BODY))
        private val FOO_EXCEPTION = Foo(-1)
        private val FOO_RELOAD = Foo(0)
        private val FOO_1 = Foo(1)
        private val FOO_2 = Foo(2)
    }
}

data class Foo(var value: Int)