package us.kostenko.architecturecomponentstmdb.common.di

import android.app.Application
import us.kostenko.architecturecomponentstmdb.common.AndroidCoroutines
import us.kostenko.architecturecomponentstmdb.common.Coroutines
import us.kostenko.architecturecomponentstmdb.common.api.retrofit.RetrofitManager
import us.kostenko.architecturecomponentstmdb.common.database.MovieDatabase
import us.kostenko.architecturecomponentstmdb.details.repository.MovieDetailRepository
import us.kostenko.architecturecomponentstmdb.details.repository.MovieDetailRepositoryImpl
import us.kostenko.architecturecomponentstmdb.details.repository.webservice.MovieWebService
import us.kostenko.architecturecomponentstmdb.master.repository.MoviesRepository
import us.kostenko.architecturecomponentstmdb.master.repository.MoviesRepositoryImpl
import us.kostenko.architecturecomponentstmdb.master.repository.webservice.MoviesWebService


/**
 * TODO: to be moved to different flavor(debug, release)
 */

object Injector: Injection {

    override fun provideDatabase(application: Application): MovieDatabase = MovieDatabase.instance(application)

    override fun provideCoroutines(): Coroutines = AndroidCoroutines()

    override fun provideMovieWebService(application: Application): MovieWebService {
        return RetrofitManager.createService(application, MovieWebService::class.java)
    }

    override fun provideMoviesWebService(application: Application): MoviesWebService {
        return RetrofitManager.createService(application, MoviesWebService::class.java)
    }

    override fun provideMoviesRepository(application: Application): MoviesRepository {
        val webService = provideMoviesWebService(application)
        val masterDao = provideDatabase(application).masterDao()
        return MoviesRepositoryImpl(webService, masterDao)
    }

    override fun provideMovieDetailRepository(application: Application): MovieDetailRepository {
        val webService = provideMovieWebService(application)
        val detailDao = provideDatabase(application).detailDao()
        val coroutines = provideCoroutines()
        return MovieDetailRepositoryImpl(webService, detailDao, coroutines)
    }
}