package us.kostenko.architecturecomponentstmdb.android

import android.app.Application
import com.facebook.stetho.Stetho
import com.facebook.stetho.okhttp3.StethoInterceptor
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import timber.log.Timber
import us.kostenko.architecturecomponentstmdb.common.di.OkHttpConfigurator

object ConcreteStethoInitializer: StethoInitializer {

    override fun init(application: Application) {
        Stetho.initialize(
                Stetho.newInitializerBuilder(application)
                        .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(application))
                        .enableDumpapp(Stetho.defaultDumperPluginsProvider(application))
                        .build())
        Timber.d("Stetho initialized")
    }

    override fun provideOkHttpClient(cache: Cache, config: OkHttpConfigurator): OkHttpClient {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        return OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .addNetworkInterceptor(StethoInterceptor())
                .cache(cache)
                .apply { config() }
                .build()
    }
}