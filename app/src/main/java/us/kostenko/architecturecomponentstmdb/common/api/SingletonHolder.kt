package us.kostenko.architecturecomponentstmdb.common.api


/**
 * Creation of a singleton that takes an argument.
 * Normally, using an object declaration in Kotlin you are guaranteed to get a safe and efficient singleton implementation.
 * But it cannot take extra arguments
 * https://medium.com/@BladeCoder/kotlin-singletons-with-argument-194ef06edd9e
 *
 * Usage example: LocalBroadcastManager.instance(context).sendBroadcast(intent)
 */
open class SingletonHolder<in A, out T>(creator: (A) -> T) {

    private var creator: ((A) -> T)? = creator
    @Volatile private var instance: T? = null

    fun instance(arg: A): T {
        val i = instance
        if (i != null) {
            return i
        }

        return synchronized(this) {
            val i2 = instance
            if (i2 != null) {
                i2
            } else {
                val created = creator!!(arg)
                instance = created
                creator = null
                created
            }
        }
    }
}