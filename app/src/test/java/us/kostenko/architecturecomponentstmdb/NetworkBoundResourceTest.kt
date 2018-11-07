package us.kostenko.architecturecomponentstmdb

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.nhaarman.mockitokotlin2.doAnswer
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.mockingDetails
import com.nhaarman.mockitokotlin2.times
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.runBlocking
import okhttp3.MediaType
import okhttp3.ResponseBody
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.any
import org.mockito.Mockito.verify
import org.mockito.Mockito.verifyNoMoreInteractions
import org.mockito.stubbing.Answer
import retrofit2.HttpException
import retrofit2.Response
import us.kostenko.architecturecomponentstmdb.common.Coroutines
import us.kostenko.architecturecomponentstmdb.common.TestCoroutines
import us.kostenko.architecturecomponentstmdb.details.viewmodel.netres.BoundResourceAdapter
import us.kostenko.architecturecomponentstmdb.details.viewmodel.netres.NetworkBoundResource
import us.kostenko.architecturecomponentstmdb.utils.mock
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicReference

class NetworkBoundResourceTest {

    // To avoid RuntimeException: Method getMainLooper in android.os.Looper not mocked
    @get:Rule val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var _saveCallResult: (Foo) -> Unit
    private lateinit var _shouldFetch: (Foo?) -> Boolean
    private lateinit var _fetchData: suspend () -> Foo
    private val dbData = MutableLiveData<Foo>()
    private val errorMessage = "Error Message"

//    private lateinit var observer: Observer<Wrapper>
    private val saved = AtomicReference<Foo>()

    private val adapter: BoundResourceAdapter<Foo, Wrapper> = mock {
        on { onProgress() } doReturn Wrapper.Loading
        on { onData(any<Foo>()) } doAnswer Answer { invocation ->  Wrapper.Success(invocation.getArgument(0)) }
        on { onError(any())} doAnswer Answer { invocation ->  Wrapper.Error(errorMessage) }
    }

    private val coroutines: Coroutines = TestCoroutines()
    private val networkBoundResource: NetworkBoundResource<Foo, Foo, Wrapper> by lazy { createNetworkBoundResource(coroutines, adapter) }

    @Test
    fun `success from network`() {
        // arrange TODO: too verbose?
        val networkResult = FOO_1

        _shouldFetch = { it == null }
        _saveCallResult = { foo ->
            saved.set(foo)
            dbData.value = FOO_1
        }
        _fetchData = { networkResult }
        dbData.value = null

        // act
        networkBoundResource.reload()

        // assert   TODO: to many checks?
        assertThat(saved.get(), `is`(networkResult))

        // TODO: too verbose?
        verify(adapter).inOrder {
            verify().onProgress()
            verify().onData(FOO_1)
        }
        verifyNoMoreInteractions(adapter)
    }

    @Test
    fun `basic from network`() {
        val saved = AtomicReference<Foo>()
        _shouldFetch = { it == null }
        _saveCallResult = { foo ->
            saved.set(foo)
            dbData.value = FOO_1
        }
        val networkResult = Foo(1)
        _fetchData = { networkResult }

        val observer = mock<Observer<Wrapper>>()
        networkBoundResource.asLiveData().observeForever(observer)

        verify(adapter).onProgress()
        verify(observer).onChanged(Wrapper.Loading)
        dbData.value = null

        assertThat(saved.get(), `is`(networkResult))

        verify(adapter).onProgress()
        verify(adapter).onData(FOO_1)
        verify(observer).onChanged(Wrapper.Success(FOO_1))

        verifyNoMoreInteractions(adapter)
        verifyNoMoreInteractions(observer)
    }

    @Test
    fun `failure from network`() {
        val saved = AtomicBoolean(false)
        _shouldFetch = { it == null }
        _saveCallResult = { saved.set(true) }

        _fetchData = { throw EXCEPTION }

        val observer = mock<Observer<Wrapper>>()
        networkBoundResource.asLiveData().observeForever(observer)

        verify(adapter).onProgress()
        verify(observer).onChanged(Wrapper.Loading)

        dbData.value = null
        assertThat(saved.get(), `is`(false))

        verify(adapter).onProgress()
//        verify(adapter).onError(errorMessage)
        verify(observer).onChanged(Wrapper.Error(errorMessage))

        verifyNoMoreInteractions(adapter)
        verifyNoMoreInteractions(observer)
    }

    @Test
    fun `db success without network`() {
        val saved = AtomicBoolean(false)
        _shouldFetch = { it == null }
        _saveCallResult = { saved.set(true) }

        val observer = mock<Observer<Wrapper>>()
        networkBoundResource.asLiveData().observeForever(observer)

        verify(adapter).onProgress()
        verify(observer).onChanged(Wrapper.Loading)

        val dbFoo = FOO_1
        dbData.value = dbFoo

        verify(adapter).onProgress()
        verify(adapter).onData(dbFoo)
        verify(observer).onChanged(Wrapper.Success(dbFoo))

        assertThat(saved.get(), `is`(false))

        val dbFoo2 = Foo(2)
        dbData.value = dbFoo2

        verify(adapter).onProgress()
        verify(adapter).onData(dbFoo2)
        verify(observer).onChanged(Wrapper.Loading)
        verify(observer).onChanged(Wrapper.Success(dbFoo2))

        verifyNoMoreInteractions(adapter)
        verifyNoMoreInteractions(observer)
    }

    @Test
    fun `db success with fetch failure`() = runBlocking {
        val dbValue = Foo(1)
        val saved = AtomicBoolean(false)
        _shouldFetch = { foo -> foo === dbValue }
        _saveCallResult = { saved.set(true) }

        _fetchData = suspend {
            throw EXCEPTION
        }

        val observer = mock<Observer<Wrapper>>()
        networkBoundResource.asLiveData().observeForever(observer)

        verify(adapter).onProgress()
        verify(observer).onChanged(Wrapper.Loading)

        dbData.value = dbValue
//        verify(adapter).onProgress()
//        verify(observer).onChanged(Wrapper.Loading)

        delay(500L)

        assertThat(saved.get(), `is`(false))
//        verify(adapter).onError(errorMessage)
        verify(observer).onChanged(Wrapper.Error(errorMessage))

        val dbValue2 = Foo(2)
        dbData.value = dbValue2

        verify(adapter, times(2)).onError(any())
//        verify(observer).onChanged(Wrapper.Error(errorMessage))
        verify(observer).onChanged(Wrapper.Error(errorMessage))
        verify(observer).onChanged(Wrapper.Loading)

        mockingDetails(adapter).invocations.forEach {
            println(it)
        }

        verifyNoMoreInteractions(adapter)
//        TODO: verifyNoMoreInteractions(observer)
    }

//    @Test
//    fun `db success with re fetch success`() = runBlocking {
//
//        val dbValue = Foo(1)
//        val dbValue2 = Foo(2)
//        val saved = AtomicReference<Foo>()
//        shouldFetch = { foo -> foo === dbValue }
//        saveCallResult = { foo ->
//            saved.set(foo)
//            dbData.setValue(dbValue2)
//        }
//
//        val networkResult = Foo(1)
//        fetchData = suspend { networkResult }
//
//        val observer = mock<Observer<Wrapper>>()
//        networkBoundResource.asLiveData().observeForever(observer)
//
//        verify(adapter).onProgress()
//        verify(observer).onChanged(Wrapper.Loading)
//        Mockito.reset(observer)
//
//        dbData.value = dbValue
//        // drain()
//        verify(adapter).onProgress()
//        verify(adapter).onData(dbValue2)
//        verify(observer).onChanged(Wrapper.Data(networkResult))
//
////        apiResponseLiveData.value = ApiResponse.create(Response.success(networkResult))
//        // drain()
//        assertThat(saved.get(), `is`(networkResult))
//        verify(adapter).onData(dbValue2)
//        verify(observer).onChanged(Wrapper.Data(dbValue2))
//
//        verifyNoMoreInteractions(observer)
//    }

    private fun createNetworkBoundResource(coroutines: Coroutines, adapter: BoundResourceAdapter<Foo, Wrapper>) =
            object : NetworkBoundResource<Foo, Foo, Wrapper>(coroutines, adapter) {

        override fun saveResult(item: Foo) = _saveCallResult(item)

        override fun shouldFetch(data: Foo?) = _shouldFetch(data)

        override fun loadFromDb(): LiveData<Foo> = dbData

        override suspend fun fetchData(): Foo? = _fetchData()
    }

    companion object {
        private const val ERROR_MESSAGE = "errorMessage"
        private val BODY = ResponseBody.create(MediaType.parse("application/json"), "{\"status_code\":34,\"status_message\":\"$ERROR_MESSAGE\"}")
        private val EXCEPTION = HttpException(Response.error<Foo>(400, BODY))
        private val FOO_1 = Foo(1)
        private val FOO_2 = Foo(2)
    }
}

data class Foo(var value: Int)

sealed class Wrapper {

    object Loading: Wrapper()

    data class Success(val data: Foo?): Wrapper()

    data class Error(val message: String?): Wrapper()
}