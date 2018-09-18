package us.kostenko.architecturecomponentstmdb.common.di

import android.app.Application
import android.arch.persistence.room.Room
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import us.kostenko.architecturecomponentstmdb.common.api.retrofit.RetrofitManager
import us.kostenko.architecturecomponentstmdb.details.repository.MovieDetailRepository
import us.kostenko.architecturecomponentstmdb.details.repository.persistance.MovieDatabase
import us.kostenko.architecturecomponentstmdb.details.repository.webservice.MovieWebService


/**
 * TODO: to be moved to different flavor(debug, release)
 */

object Injector: Injection {

    override fun provideOkHttpClient(cache: Cache, config: OkHttpConfigurator): OkHttpClient {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        return OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .cache(cache)
                .apply { config() }
                .build()
    }

    override fun provideMovieDetailRepository(application: Application): MovieDetailRepository {
        val webService = RetrofitManager.createService(application, MovieWebService::class.java)
        val movieDao = Room.databaseBuilder(application, MovieDatabase::class.java, "movie-database").build().movieDao()
        return MovieDetailRepository(webService, movieDao)
    }
}