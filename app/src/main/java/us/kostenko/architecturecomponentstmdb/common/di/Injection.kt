@file:JvmName("InjectorKt")

package us.kostenko.architecturecomponentstmdb.common.di

import android.content.Context
import okhttp3.OkHttpClient
import us.kostenko.architecturecomponentstmdb.common.Coroutines
import us.kostenko.architecturecomponentstmdb.common.database.MovieDatabase
import us.kostenko.architecturecomponentstmdb.details.model.Movie
import us.kostenko.architecturecomponentstmdb.details.repository.FRESH_TIMEOUT_MINUTES
import us.kostenko.architecturecomponentstmdb.details.repository.MovieDetailRepository
import us.kostenko.architecturecomponentstmdb.details.repository.MovieDetailRepositoryImpl
import us.kostenko.architecturecomponentstmdb.details.repository.webservice.MovieWebService
import us.kostenko.architecturecomponentstmdb.details.viewmodel.MovieDetailViewModel
import us.kostenko.architecturecomponentstmdb.master.repository.MoviesRepository
import us.kostenko.architecturecomponentstmdb.master.repository.MoviesRepositoryImpl
import us.kostenko.architecturecomponentstmdb.master.repository.webservice.MoviesWebApi
import us.kostenko.architecturecomponentstmdb.master.viewmodel.MovieItemViewModel
import us.kostenko.architecturecomponentstmdb.master.viewmodel.MoviesViewModel
import java.util.Date

typealias OkHttpConfigurator = OkHttpClient.Builder.() -> Unit

abstract class Injection {

    abstract fun provideCoroutines(): Coroutines

    abstract fun provideDatabase(context: Context): MovieDatabase

    abstract fun provideMasterWebService(context: Context): MoviesWebApi

    abstract fun provideDetailWebService(context: Context): MovieWebService

    fun movieDetailViewModel(context: Context, timeout: Int = FRESH_TIMEOUT_MINUTES): MovieDetailViewModel {
        return MovieDetailViewModel(Injector.provideCoroutines(), provideMovieDetailRepository(context, timeout))
    }

    fun itemViewModel(): MovieItemViewModel {
        return MovieItemViewModel()
    }

    fun moviesViewModel(context: Context): MoviesViewModel {
        return MoviesViewModel(provideMoviesRepository(context))
    }

    private fun provideMoviesRepository(context: Context): MoviesRepository {
        val webService = Injector.provideMasterWebService(context)
        val masterDao = Injector.provideDatabase(context).masterDao()
        val coroutines = Injector.provideCoroutines()
        return MoviesRepositoryImpl(webService, masterDao, coroutines)
    }


    private fun provideMovieDetailRepository(context: Context, timeout: Int = FRESH_TIMEOUT_MINUTES): MovieDetailRepository {
        val webService = Injector.provideDetailWebService(context)
        val detailDao = Injector.provideDatabase(context).detailDao()
        val coroutines = Injector.provideCoroutines()
        return MovieDetailRepositoryImpl(webService, detailDao, coroutines, timeout)
    }
}

fun buildMovie(id: Int,
               liked: Boolean = false,
               sort: Int = 1,
               prefix: String = "",
               dateUpdate: Date = Date()) = Movie(id, "${prefix}Title", "${prefix}Date", "${prefix}poster", "${prefix}backdrop",
                       "${prefix}overview", "${prefix}eng", "${prefix}origTitle",
                       null, dateUpdate).apply {
        this.liked = liked
        this.sort = sort
}