package us.kostenko.architecturecomponentstmdb.android

import android.util.Log
import android.util.Log.INFO
import android.util.Log.WARN
import com.crashlytics.android.Crashlytics
import timber.log.Timber

object TimberInitializerImpl: TimberInitializer {

    override fun init() {
        Timber.plant(CrashReportingTree())
    }

    /** A tree which logs important information for crash reporting.  */
    private class CrashReportingTree : Timber.Tree() {
        override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
            if (priority == Log.VERBOSE || priority == Log.DEBUG) {
                return
            }

            Crashlytics.log(priority, tag, message)

            if (t != null) {
                if (priority == Log.ERROR) {
                    Crashlytics.logException(t)
                } else if (priority == Log.WARN) {
                    Crashlytics.log(WARN, tag, t.message)
                }
            }
        }

        fun isLoggable(priority: Int, tag: String?) = priority >= INFO
    }
}