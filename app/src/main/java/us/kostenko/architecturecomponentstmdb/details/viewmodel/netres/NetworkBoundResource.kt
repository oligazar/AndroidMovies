package us.kostenko.architecturecomponentstmdb.details.viewmodel.netres

import androidx.annotation.MainThread
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import timber.log.Timber
import us.kostenko.architecturecomponentstmdb.common.Coroutines
import us.kostenko.architecturecomponentstmdb.common.api.retrofit.asRetrofitException
import us.kostenko.architecturecomponentstmdb.details.model.MovieError

/**
 * A generic class that can provide a resource backed by both the sqlite database and the network.
 *
 *
 * You can read more about it in the [Architecture
 * Guide](https://developer.android.com/arch).
 * @param <ResultType>
 * @param <RequestType>
</RequestType></ResultType> */

enum class Status { SUCCESS, ERROR, LOADING }

interface BoundResourceAdapter<Data, Wrapper> {

    fun onProgress(): Wrapper

    fun onData(data: Data?): Wrapper

    fun onError(exception: Exception?): Wrapper

//    fun setEmpty(): Wrapper
}

open class StateAdapter<T>: BoundResourceAdapter<T, State<T>> {

    private var state: State<T> = State.InitialLoading

    override fun onData(data: T?): State<T> {
        state = state.showData(data)
        return state
    }

    override fun onError(exception: Exception?): State<T> {
        val message = exception?.apiMessage<MovieError> { statusMessage }
        state = state.showError(message)
        return state
    }

    override fun onProgress(): State<T> {
        state = state.showLoading()
        return state
    }
}

abstract class NetworkBoundResource<ResultType, RequestType, Wrapper>
@MainThread constructor(private val coroutines: Coroutines, private val adapter: BoundResourceAdapter<ResultType, Wrapper>) {

    private val result = MediatorLiveData<Wrapper>()
    private lateinit var dbSource: LiveData<ResultType>

    fun reload() {
        result.value = adapter.onProgress()
        dbSource = loadFromDb()
        result.removeSource(source())

        result.addSourceOnce(true, dbSource) { data ->
            if (shouldFetch(data)) {
                fetchFromNetwork()
            } else result.addSource(dbSource) { newData ->
                setValue(adapter.onData(newData))
            }
        }
    }

    private fun fetchFromNetwork() = coroutines {
        process({
                   result.removeSource(source())
                   val newData = fetchData()
                   newData?.let { saveResult(newData) }
                }, { status, once, e ->
            // this callback is called upon LOADING, SUCCESS or ERROR events
            coroutines.onUi {
                result.addSourceOnce(once, source()) { newData ->
                    // TODO: Here might be a problem with over deconstructing the error
                    setValue(wrapperFromParts(status, newData, e))
                }
            }
        })
    }

    @Suppress("UNCHECKED_CAST")
    fun asLiveData() = result as LiveData<Wrapper>

    @WorkerThread
    protected abstract fun saveResult(item: RequestType)

    @MainThread
    protected abstract fun shouldFetch(data: ResultType?): Boolean

    @MainThread
    protected abstract fun loadFromDb(): LiveData<ResultType>

    @MainThread
    protected abstract suspend fun fetchData(): RequestType?

    @MainThread
    private fun setValue(newValue: Wrapper) {
        if (result.value != newValue) {
            result.value = newValue
        }
    }

    private inline fun process(f: () -> Unit, result: (status: Status, once: Boolean, e: Exception?) -> Unit) {
        result(Status.LOADING, true, null)
        try {
            f()
            result(Status.SUCCESS, false, null)
        } catch (e: Exception) {
            println(e)
            result(Status.ERROR, false, e)
        }
    }

    private fun wrapperFromParts(status: Status, newData: ResultType?, exception: Exception?): Wrapper {
        return when (status) {
            Status.ERROR -> adapter.onError(exception)
            Status.LOADING -> adapter.onProgress()
            Status.SUCCESS -> adapter.onData(newData)
        }
    }

    private fun source(status: Status = Status.LOADING) =
            if (status == Status.ERROR || status == Status.LOADING) dbSource else loadFromDb()
}

fun <T, S> MediatorLiveData<T>.addSourceOnce(once: Boolean, source: LiveData<S>, observer: (S?) -> Unit) {
    addSource(source) { data ->
        if (once) removeSource(source)
        observer(data)
    }
}

inline fun <reified T>Exception.apiMessage(extractMessage: T.() -> String): String? {
    val apiException = asRetrofitException().getErrorBodyAs(T::class.java)
    val message =  apiException?.extractMessage() ?: message
    Timber.d("Exception: $this, message: $message")
    return message
}