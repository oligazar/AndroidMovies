package us.kostenko.architecturecomponentstmdb.common.di

import android.content.Context
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

    override fun provideDatabase(context: Context): MovieDatabase = MovieDatabase.instance(context)

    override fun provideCoroutines(): Coroutines = AndroidCoroutines()

    override fun provideMasterWebService(context: Context): MoviesWebService {
        return RetrofitManager.createService(context, MoviesWebService::class.java)
    }

    override fun provideDetailWebService(context: Context): MovieWebService {
        return RetrofitManager.createService(context, MovieWebService::class.java)
    }

    override fun provideMoviesRepository(context: Context): MoviesRepository {
        val webService = provideMasterWebService(context)
        val masterDao = provideDatabase(context).masterDao()
        return MoviesRepositoryImpl(webService, masterDao)
    }

    override fun provideMovieDetailRepository(context: Context): MovieDetailRepository {
        val webService = provideDetailWebService(context)
        val detailDao = provideDatabase(context).detailDao()
        val coroutines = provideCoroutines()
        return MovieDetailRepositoryImpl(webService, detailDao, coroutines)
    }
}