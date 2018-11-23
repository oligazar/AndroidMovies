package us.kostenko.architecturecomponentstmdb.android

import timber.log.Timber

object TimberInitializerImpl: TimberInitializer {

    override fun init() {
        Timber.plant(object: Timber.DebugTree() {
            override fun createStackElementTag(element: StackTraceElement): String? {
                return String.format("Timber(%s:%s)",
                                     super.createStackElementTag(element),
                                     element.lineNumber)
            }
        })
    }
}