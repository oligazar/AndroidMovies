package us.kostenko.architecturecomponentstmdb.common

import android.os.AsyncTask
import kotlinx.coroutines.experimental.CoroutineScope
import kotlinx.coroutines.experimental.Dispatchers
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.android.Main
import kotlinx.coroutines.experimental.asCoroutineDispatcher
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.newSingleThreadContext
import kotlinx.coroutines.experimental.runBlocking
import kotlinx.coroutines.experimental.withContext
import kotlin.coroutines.experimental.CoroutineContext


/**
 * TODO:
 * - сделать их с помощью CoroutineContext?
 */

interface Coroutines {

    operator fun invoke(f: suspend CoroutineScope.() -> Unit)

    fun cancel()

    suspend fun onUi(f: suspend CoroutineScope.() -> Unit)
}

class AndroidCoroutines : Coroutines, CoroutineScope {

    private val job = Job()

    override fun cancel() {
        job.cancel()
    }

    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Default

    override operator fun invoke(f: suspend CoroutineScope.() -> Unit) {
        launch(block = f)
    }

    override suspend fun onUi(f: suspend CoroutineScope.() -> Unit) {
        withContext(Dispatchers.Main, block = f)
    }
}

class TestCoroutines : Coroutines {

    override fun invoke(f: suspend CoroutineScope.() -> Unit) {
        runBlocking(block = f)
    }

    override fun cancel() { }

    override suspend fun onUi(f: suspend CoroutineScope.() -> Unit) {
        runBlocking(block = f)
    }
}

class NewTestCoroutines : Coroutines, CoroutineScope {

    // TestUIContext, TestCoroutineContext
    private val job = Job()

    override fun cancel() {
        job.cancel()
    }

    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Default

    override operator fun invoke(f: suspend CoroutineScope.() -> Unit) {
        launch(block = f)
    }

    override suspend fun onUi(f: suspend CoroutineScope.() -> Unit) {
        withContext(newSingleThreadContext("Main single thread"), block = f)
    }
}

class AndroidTestCoroutines : Coroutines, CoroutineScope {

    private val job = Job()

    override fun cancel() {
        job.cancel()
    }

    override val coroutineContext: CoroutineContext
        get() = job + AsyncTask.THREAD_POOL_EXECUTOR.asCoroutineDispatcher()

    override operator fun invoke(f: suspend CoroutineScope.() -> Unit) {
        launch(block = f)
    }

    override suspend fun onUi(f: suspend CoroutineScope.() -> Unit) {
        withContext(Dispatchers.Main, block = f)
    }
}