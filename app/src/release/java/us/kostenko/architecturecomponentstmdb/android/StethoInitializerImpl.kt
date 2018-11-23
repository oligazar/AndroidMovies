package us.kostenko.architecturecomponentstmdb.android

import android.app.Application
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import us.kostenko.architecturecomponentstmdb.common.di.OkHttpConfigurator

object StethoInitializerImpl: StethoInitializer {

    override fun init(application: Application) {
        /* no-op */
    }

    /* No Stetho interceptor in release */
    override fun provideOkHttpClient(cache: Cache, config: OkHttpConfigurator): OkHttpClient {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        return OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .cache(cache)
                .apply { config() }
                .build()
    }
}