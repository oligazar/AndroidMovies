package us.kostenko.architecturecomponentstmdb.details.viewmodel.netres

import us.kostenko.architecturecomponentstmdb.common.view.Event

sealed class State<out T> {

    object InitialLoading: State<Nothing>()

    data class Retry(val message: String?): State<Nothing>()

    data class Success<T>(val data: T?): State<T>()

    object Loading: State<Nothing>()

    data class Error(val message: Event<String?>): State<Nothing>()



    fun showLoading(): State<T> = if (this is InitialLoading || this is Retry) InitialLoading else Loading

    fun <T>showData(data: T?): State<T> = if (this is Retry || this is Error) this as State<T> else Success(data)

    fun showError(message: String?): State<T> = if (this is InitialLoading || this is Retry) Retry(message) else Error(Event(message))
}