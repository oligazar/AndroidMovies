package us.kostenko.architecturecomponentstmdb.android

import android.app.Application
import okhttp3.Cache
import okhttp3.OkHttpClient
import us.kostenko.architecturecomponentstmdb.common.di.OkHttpConfigurator

interface StethoInitializer {
    fun init(application: Application)
    fun provideOkHttpClient(cache: Cache, config: OkHttpConfigurator): OkHttpClient
}