@file:JvmName("InjectorKt")

package us.kostenko.architecturecomponentstmdb.common.di

import android.content.Context
import okhttp3.OkHttpClient
import us.kostenko.architecturecomponentstmdb.common.Coroutines
import us.kostenko.architecturecomponentstmdb.common.database.MovieDatabase
import us.kostenko.architecturecomponentstmdb.details.repository.MovieDetailRepository
import us.kostenko.architecturecomponentstmdb.details.repository.webservice.MovieWebService
import us.kostenko.architecturecomponentstmdb.master.repository.MoviesRepository
import us.kostenko.architecturecomponentstmdb.master.repository.webservice.MoviesWebService

typealias OkHttpConfigurator = OkHttpClient.Builder.() -> Unit

interface Injection {

    fun provideMoviesRepository(context: Context): MoviesRepository

    fun provideDatabase(context: Context): MovieDatabase

    fun provideCoroutines(): Coroutines

    fun provideMasterWebService(context: Context): MoviesWebService

    fun provideDetailWebService(context: Context): MovieWebService

    fun provideMovieDetailRepository(context: Context): MovieDetailRepository
}