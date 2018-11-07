package us.kostenko.architecturecomponentstmdb.utils

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import us.kostenko.architecturecomponentstmdb.details.viewmodel.netres.State

object ApiUtil {
    fun <T : Any> successCall(data: T) = createCall(State.Success(data))

    fun <T : Any> createCall(response: State<T>) = MutableLiveData<State<T>>().apply {
        value = response
    } as LiveData<State<T>>
}