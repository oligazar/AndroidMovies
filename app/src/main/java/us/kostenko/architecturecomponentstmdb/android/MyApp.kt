package us.kostenko.architecturecomponentstmdb.android

import android.app.Application
import com.crashlytics.android.Crashlytics
import io.fabric.sdk.android.Fabric
import org.koin.android.ext.android.startKoin
import us.kostenko.architecturecomponentstmdb.di.onlineAppModules


class MyApp: Application() {

    override fun onCreate() {
        super.onCreate()
        ConcreteTimberInitializer.init()
        ConcreteStethoInitializer.init(this)
        Fabric.with(this, Crashlytics())

        startKoin(this, onlineAppModules)
    }
}