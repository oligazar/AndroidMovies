package us.kostenko.architecturecomponentstmdb.details.viewmodel.netres

import androidx.annotation.MainThread
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import us.kostenko.architecturecomponentstmdb.common.Coroutines
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

//enum class Status { SUCCESS, ERROR, LOADING }
//
//interface BoundResourceAdapter<Data, Wrapper> {
//
//    fun onProgress(): Wrapper
//
//    fun onData(data: Data?): Wrapper
//
//    fun onError(message: String?): Wrapper
//
////    fun setEmpty(): Wrapper
//}
//
//open class StateAdapter<T>: BoundResourceAdapter<T, State<T>> {
//
//    private var state: State<T> = State.InitialLoading
//
//    override fun onData(data: T?): State<T> {
//        state = state.showData(data)
//        return state
//    }
//
//    override fun onError(message: String?): State<T> {
//        state = state.showError(message)
//        return state
//    }
//
//    override fun onProgress(): State<T> {
//        state = state.showLoading()
//        return state
//    }
//}

abstract class NewNetworkBoundResource<ResultType, RequestType, Wrapper>
@MainThread constructor(private val coroutines: Coroutines, private val adapter: BoundResourceAdapter<ResultType, Wrapper>) {

    private val result = MediatorLiveData<Wrapper>()

    fun process() {
        // show loading status
        adapter.onProgress()
        @Suppress("LeakingThis")
        val dbSource = loadFromDb()
        // retrieve data from db to check against it
        result.addSourceOnce(true, dbSource) { data ->
            if (shouldFetch(data)) {
                // fetch from network only if shouldFetch return true
                fetchFromNetwork(dbSource)
            } else {
                // if there's no need to fetch - listen to db and return success
                result.addSource(dbSource) { newData ->
                    newData?.let { setValue(adapter.onData(newData)) }
                }
            }
        }
    }

    private fun fetchFromNetwork(dbSource: LiveData<ResultType>) = coroutines {
        fun source(status: Status = Status.LOADING) = if (status == Status.ERROR || status == Status.LOADING) dbSource else loadFromDb()

        handle({ fetchAndSave(source()) }) { status, once, e ->
            // this callback is called upon LOADING, SUCCESS or ERROR events
            wrapSource(source(status), status, once, e)
        }
    }

    private suspend fun fetchAndSave(source: LiveData<ResultType>) {
        result.removeSource(source)
        fetchData()?.let { newData -> saveCallResult(newData) }
    }

    private suspend fun wrapSource(source: LiveData<ResultType>, status: Status, once: Boolean, e: Exception?) = coroutines.onUi {
        result.addSourceOnce(once, source) { newData ->

            // TODO: Here might be a problem with over deconstructing the error
            val message = e?.apiMessage<MovieError> { statusMessage }

            if (e != null) onFetchFailed()
            // set value of live data
            setValue(wrapperFromParts(status, newData, e))
        }
    }

    protected open fun onFetchFailed() {}

    @WorkerThread
    protected open fun processResponse(response: ApiSuccessResponse<RequestType>) = response.body

    @WorkerThread
    protected abstract fun saveCallResult(item: RequestType)

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

    private inline fun handle(call: () -> Unit, result: (status: Status, once: Boolean, e: Exception?) -> Unit) {
        result(Status.LOADING, true, null)
        try {
            call()
            result(Status.SUCCESS, false, null)
        } catch (e: Exception) {
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
}