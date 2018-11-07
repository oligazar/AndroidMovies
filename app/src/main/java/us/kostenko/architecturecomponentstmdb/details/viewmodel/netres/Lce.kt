package us.kostenko.architecturecomponentstmdb.details.viewmodel.netres

/**
 * A generic class that holds a value with its loading status.
 * Lce stand for Life Cycle Events
 * @param <T>
</T> */
data class Lce<out T>(val status: Status, val data: T?, val message: String?) {

    // Service class to pass data from handle method
    class Parts(val status: Status, val once: Boolean, val e: Exception? = null)

    companion object {
        fun <T> success(data: T?): Lce<T> {
            return Lce(Status.SUCCESS, data, null)
        }

        fun <T> error(msg: String?, data: T?): Lce<T> {
            return Lce(Status.ERROR, data, msg)
        }

        fun <T> loading(data: T?): Lce<T> {
            return Lce(Status.LOADING, data, null)
        }

        inline fun handle(f: () -> Unit, copy: (Parts) -> Unit) {
            copy(Parts(Status.LOADING, true))
            try {
                f()
                copy(Parts(Status.SUCCESS, false))
            } catch (e: Exception) {
                copy(Parts(Status.ERROR, false, e))
            }
        }
    }
}

/**
 * Status of a resource that is provided to the UI.
 *
 * These are usually created by the Repository classes where they return
 * `LiveData<Lce<T>>` to pass back the latest data to the UI with its fetch status.
 */

