package us.kostenko.architecturecomponentstmdb.android

import android.app.Application
import com.facebook.stetho.Stetho
import timber.log.Timber

object ConcreteStethoInitializer: StethoInitializer {

    override fun init(application: Application) {
        Stetho.initialize(
                Stetho.newInitializerBuilder(application)
                        .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(application))
                        .enableDumpapp(Stetho.defaultDumperPluginsProvider(application))
                        .build())
        Timber.d("Stetho initialized")
    }
}