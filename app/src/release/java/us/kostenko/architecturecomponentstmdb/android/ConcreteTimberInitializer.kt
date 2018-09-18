package us.kostenko.architecturecomponentstmdb.android

import android.util.Log
import android.util.Log.INFO
import timber.log.Timber

object ConcreteTimberInitializer: TimberInitializer {

    override fun init() {
        Timber.plant(CrashReportingTree())
    }

    /** A tree which logs important information for crash reporting.  */
    private class CrashReportingTree : Timber.Tree() {
        override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
            if (priority == Log.VERBOSE || priority == Log.DEBUG) {
                return
            }

            FakeCrashLibrary.log(priority, tag, message)

            if (t != null) {
                if (priority == Log.ERROR) {
                    FakeCrashLibrary.logError(t)
                } else if (priority == Log.WARN) {
                    FakeCrashLibrary.logWarning(t)
                }
            }
        }

        fun isLoggable(priority: Int, tag: String?) = priority >= INFO
    }
}