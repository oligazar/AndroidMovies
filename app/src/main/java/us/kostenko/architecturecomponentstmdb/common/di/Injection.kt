@file:JvmName("InjectorKt")

package us.kostenko.architecturecomponentstmdb.common.di

import android.app.Application
import okhttp3.Cache
import okhttp3.OkHttpClient
import us.kostenko.architecturecomponentstmdb.details.repository.MovieDetailRepository

typealias OkHttpConfigurator = OkHttpClient.Builder.() -> Unit

interface Injection {

    fun provideOkHttpClient(cache: Cache, config: OkHttpConfigurator): OkHttpClient

    fun provideMovieDetailRepository(application: Application): MovieDetailRepository
}