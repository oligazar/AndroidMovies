package us.kostenko.architecturecomponentstmdb.common.utils

import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders

inline fun <VM : ViewModel> viewModelFactory(crossinline f: () -> VM) =
        object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(aClass: Class<T>):T = f() as T
        }

inline fun <reified VM : ViewModel> Fragment.viewModelProvider(crossinline provider: () -> VM) = lazy {
    ViewModelProviders.of(this, object : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T1 : ViewModel> create(aClass: Class<T1>) =
                provider() as T1
    }).get(VM::class.java)
}

inline fun <T> LiveData<T>.observe(owner: LifecycleOwner, crossinline observe: (T) -> Unit) {
    observe(owner, Observer { observe(it) })
}