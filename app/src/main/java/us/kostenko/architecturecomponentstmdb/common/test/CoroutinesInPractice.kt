package us.kostenko.architecturecomponentstmdb.common.test

import android.location.Location
import kotlinx.coroutines.experimental.CoroutineScope
import kotlinx.coroutines.experimental.Dispatchers
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.android.Main
import kotlinx.coroutines.experimental.channels.Channel
import kotlinx.coroutines.experimental.channels.ReceiveChannel
import kotlinx.coroutines.experimental.channels.SendChannel
import kotlinx.coroutines.experimental.coroutineScope
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.selects.select
import kotlin.coroutines.experimental.CoroutineContext

data class Reference(val name: String) {
    fun resolveLocation() = Location("provider")
}

data class LocContent(val location: Location, val content: Content)

data class Content(val name: String)

class SomethingWithLifecycle: CoroutineScope {

    private val job = Job()

    fun onClose() { job.cancel() }

    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main

    fun doSomething() {
        val references = Channel<Reference>()
        processReferences(references)
    }
}

/**
 * Suspedned function - doesn't return until the job is done
 * CoroutineScope extension function returns immediately and launches background coroutine in the scope
 */
suspend fun downloadContent(location: Location): Content = Content("content")

const val N_WORKERS = 5

fun CoroutineScope.processReferences(
        references: ReceiveChannel<Reference>
) { // communication primitives
    val locations = Channel<Location>()
    val contents = Channel<LocContent>()
    repeat(N_WORKERS) { worker(locations, contents) }
    downloader(references, locations, contents)
}

/**
 * Worker pool
 */
fun CoroutineScope.worker(
        locations: ReceiveChannel<Location>,
        contents: SendChannel<LocContent>
) = launch {
    // fan-out fashion (по одному на каждый worker)
    for (loc in locations) {
        val content = downloadContent(loc)
        contents.send(LocContent(loc, content))
    }
}

/**
 * Creates downloader
 * It is CONVENTION to make a function extend CoroutineScope if you need to laouch() inside it
 */
fun CoroutineScope.downloader(
        references: ReceiveChannel<Reference>,
        locations: SendChannel<Location>,
        contents: ReceiveChannel<LocContent>
) = launch {
        val requested = mutableMapOf<Location, MutableList<Reference>>()
        while (true) {
            select<Unit> {
                references.onReceive { ref ->
                    val loc = ref.resolveLocation()
                    val refs = requested[loc]
                    if (refs == null) {
                        requested[loc] = mutableListOf(ref)
                        locations.send(loc)
                    } else {
                        refs.add(ref)
                    }
                }
                contents.onReceive { (loc, content) ->
                    val refs = requested.remove(loc)!!
                    for (ref in refs) {
                        processContent(ref, content)
                    }
                }
            }
        }
    }

fun processContent(reference: Reference, content: Content) { }


suspend fun processReferences(refs: List<Reference>) = coroutineScope {
    for (ref in refs) {
        val location = ref.resolveLocation()
        launch {
            val content = downloadContent(location)
            processContent(ref, content)
        }
    }
}

//fun CoroutineScope.downloader(
//        references: ReceiveChannel<Reference>,
//        locations: SendChannel<Location>,
//        contents:
//) = launch {
//    val requested = mutableSetOf<Location>()
//    for (ref in references) {
//        val location = ref.resolveLocation()
//        if (requested.add(location)) {
//            locations.send(location)
//        }
//    }
//}