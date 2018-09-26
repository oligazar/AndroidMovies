package us.kostenko.architecturecomponentstmdb.common.view

class Event<out T>(private val value: T) {

    var hasBeenHandled = false
        private set // Allow external read but not write

    /**
     * Returns the content and prevents its use again.
     */
    fun getValueIfNotHandled(): T? {
        return if (hasBeenHandled) null
        else {
            hasBeenHandled = true
            value
        }
    }

    /**
     * Returns the content, even if it's already been handled.
     */
    fun peekContent(): T = value
}