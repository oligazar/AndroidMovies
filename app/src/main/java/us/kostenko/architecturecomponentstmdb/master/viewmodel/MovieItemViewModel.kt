package us.kostenko.architecturecomponentstmdb.master.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import timber.log.Timber
import us.kostenko.architecturecomponentstmdb.common.view.Event

class MovieItemViewModel: ViewModel() {

    val showDetails = MutableLiveData<Event<Int>>()

    fun showDetails(movieId: Int) {
        Timber.d("movieId: $movieId")
        showDetails.value = Event(movieId)
    }
}