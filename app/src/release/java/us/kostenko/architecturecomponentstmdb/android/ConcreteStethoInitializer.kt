package us.kostenko.architecturecomponentstmdb.android

import android.app.Application

object ConcreteStethoInitializer: StethoInitializer {

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