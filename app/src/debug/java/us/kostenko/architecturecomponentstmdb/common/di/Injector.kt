package us.kostenko.architecturecomponentstmdb.common.di

import android.app.Application
import com.facebook.stetho.okhttp3.StethoInterceptor
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import us.kostenko.architecturecomponentstmdb.common.Coroutines
import us.kostenko.architecturecomponentstmdb.common.api.retrofit.RetrofitManager
import us.kostenko.architecturecomponentstmdb.common.database.MovieDatabase
import us.kostenko.architecturecomponentstmdb.details.repository.MovieDetailRepository
import us.kostenko.architecturecomponentstmdb.details.repository.MovieDetailRepositoryImpl
import us.kostenko.architecturecomponentstmdb.details.repository.webservice.MovieWebService
import us.kostenko.architecturecomponentstmdb.master.repository.MoviesRepository
import us.kostenko.architecturecomponentstmdb.master.repository.webservice.MoviesWebService

object Injector: Injection {

    override fun provideOkHttpClient(cache: Cache, config: OkHttpConfigurator): OkHttpClient {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        return OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .addNetworkInterceptor(StethoInterceptor())
                .cache(cache)
                .apply { config() }
                .build()
    }

    override fun provideMovieDetailRepository(application: Application, coroutines: Coroutines): MovieDetailRepository {
        val webService = RetrofitManager.createService(application, MovieWebService::class.java)
        val detailDao = MovieDatabase.instance(application).detailDao()
        return MovieDetailRepositoryImpl(webService, detailDao, coroutines)
    }

    override fun provideMoviesRepository(application: Application): MoviesRepository {
        val webService = RetrofitManager.createService(application, MoviesWebService::class.java)
        val masterDao = MovieDatabase.instance(application).masterDao()
        return MoviesRepository(webService, masterDao)
    }
}