package us.kostenko.architecturecomponentstmdb.details.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import assertk.assertions.isInstanceOf
import com.nhaarman.mockitokotlin2.InOrderOnType
import okhttp3.MediaType
import okhttp3.ResponseBody
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import us.kostenko.architecturecomponentstmdb.common.Coroutines
import us.kostenko.architecturecomponentstmdb.common.TestCoroutines
import us.kostenko.architecturecomponentstmdb.details.viewmodel.netres.NetworkBoundResource
import us.kostenko.architecturecomponentstmdb.details.viewmodel.netres.State
import us.kostenko.architecturecomponentstmdb.details.viewmodel.netres.StateAdapter
import java.util.concurrent.atomic.AtomicReference
import kotlin.properties.Delegates.observable

class NetworkBoundResourceWithAdapterTest {

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
    private val states = mutableListOf<State<Foo>>()

    private val coroutines: Coroutines = TestCoroutines()
    private val adapter = StateAdapter<Foo>()
    private val networkBoundResource: NetworkBoundResource<Foo, Foo, State<Foo>> by lazy { createNetworkBoundResource(coroutines, adapter) }

    @Before
    fun init() {
        networkBoundResource.asLiveData().observeForever { states += it }
        saved.set(null)
    }

    @Test
    fun `reload, when db is not empty, should return success`() {
        // arrange
        internetValues = sequenceOf(
                FOO_2)
        dbData.value = FOO_1

        // act
        networkBoundResource.reload()
        networkBoundResource.reload()

        // assert
        states shouldContain {
            initialLoading().success()
                    .loading().success()
                    .finish()
        }
    }

    @Test
    fun `reload, when db is empty, should return success`() {
        internetValues = sequence {
            yield(FOO_1)
            yield(FOO_2)
        }
        dbData.value = null

        networkBoundResource.reload()
        networkBoundResource.reload()

        states shouldContain {
            initialLoading().success()
                    .loading().success()
                    .finish()
        }
    }

    @Test
    fun `reload, when db is empty, should return error`() {

        internetValues = sequence {
            yield(FOO_RELOAD)
            yield(FOO_EXCEPTION)
            yield(FOO_1)
        }
        dbData.value = null

        networkBoundResource.reload()
        networkBoundResource.reload()
        networkBoundResource.reload()

        states shouldContain {
            initialLoading().success()
                    .loading().error()
                    .loading().success()
                    .finish()
        }
    }

    @Test
    fun `reload, when db is empty, should return first error from network`() {

        internetValues = sequence {
            yield(FOO_EXCEPTION)
            yield(FOO_RELOAD)
            yield(FOO_EXCEPTION)
        }
        dbData.value = null

        networkBoundResource.reload()
        networkBoundResource.reload()
        networkBoundResource.reload()

        states shouldContain {
            initialLoading().retry()
                    .initialLoading().success()
                    .loading().error()
                    .finish()
        }
    }

    private fun createNetworkBoundResource(coroutines: Coroutines, adapter: StateAdapter<Foo>) =
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

class StateTester(private val invocation: Int, private val list: List<State<Foo>>) {
    fun initialLoading(): StateTester = check(State.InitialLoading::class.java)
    fun retry(): StateTester = check(State.Retry::class.java)
    fun success(): StateTester = check(State.Success::class.java)
    fun loading(): StateTester = check(State.Loading::class.java)
    fun error(): StateTester = check(State.Error::class.java)
    fun finish() { assertThat(list.isEmpty()).describedAs("Less invocations than expected, current invocation: $invocation") }

    private fun check(clazz: Class<*>): StateTester {
        assertThat(list).isNotEmpty.describedAs("More invocations than expected, current invocation: $invocation, class: $clazz")
        assertk.assert(list[0], "invocation: $invocation").isInstanceOf(clazz)

        return StateTester(invocation + 1,
                                                                                     list.subList(1, list.size))
    }
}

infix fun List<State<Foo>>.shouldContain(f: StateTester.() -> Unit) = StateTester(
        1, this).f()

inline fun <T> T.inOrder(block: InOrderOnType<T>.() -> Unit) {
    block.invoke(InOrderOnType(this))
}