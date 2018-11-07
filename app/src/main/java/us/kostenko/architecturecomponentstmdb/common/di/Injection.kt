@file:JvmName("InjectorKt")

package us.kostenko.architecturecomponentstmdb.common.di

import android.app.Application
import okhttp3.Cache
import okhttp3.OkHttpClient
import us.kostenko.architecturecomponentstmdb.common.Coroutines
import us.kostenko.architecturecomponentstmdb.details.repository.MovieDetailRepository
import us.kostenko.architecturecomponentstmdb.master.repository.MoviesRepository

typealias OkHttpConfigurator = OkHttpClient.Builder.() -> Unit

interface Injection {

    fun provideOkHttpClient(cache: Cache, config: OkHttpConfigurator): OkHttpClient

    fun provideMovieDetailRepository(application: Application, coroutines: Coroutines): MovieDetailRepository

    fun provideMoviesRepository(application: Application): MoviesRepository
}