package us.kostenko.architecturecomponentstmdb.android

import android.app.Application
import com.crashlytics.android.Crashlytics
import io.fabric.sdk.android.Fabric


class MyApp: Application() {

    override fun onCreate() {
        super.onCreate()
        TimberInitializerImpl.init()
        StethoInitializerImpl.init(this)
        Fabric.with(this, Crashlytics())
    }
}