package us.kostenko.architecturecomponentstmdb.master.repository.inMemory.byPage

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource

class MoviesDataSourceFactory<K, V, T: DataSource<K, V>>(private val factory: () -> T): DataSource.Factory<K, V>() {

    val sourceLiveData = MutableLiveData<T>()

    override fun create(): T {
        val source = factory()
        sourceLiveData.postValue(source)
        return source
    }
}