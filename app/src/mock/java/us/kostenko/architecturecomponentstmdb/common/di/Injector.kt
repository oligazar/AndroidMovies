package us.kostenko.architecturecomponentstmdb.common.di

import android.content.Context
import androidx.room.Room
import kotlinx.coroutines.Deferred
import us.kostenko.architecturecomponentstmdb.common.Coroutines
import us.kostenko.architecturecomponentstmdb.common.TestCoroutines
import us.kostenko.architecturecomponentstmdb.common.database.MovieDatabase
import us.kostenko.architecturecomponentstmdb.details.model.Movie
import us.kostenko.architecturecomponentstmdb.details.repository.MovieDetailRepository
import us.kostenko.architecturecomponentstmdb.details.repository.MovieDetailRepositoryImpl
import us.kostenko.architecturecomponentstmdb.details.repository.webservice.MovieWebService
import us.kostenko.architecturecomponentstmdb.master.model.Movies
import us.kostenko.architecturecomponentstmdb.master.repository.MoviesRepository
import us.kostenko.architecturecomponentstmdb.master.repository.MoviesRepositoryImpl
import us.kostenko.architecturecomponentstmdb.master.repository.webservice.MoviesWebService


/**
 * Injections for mock build variant
 */

object Injector: Injection {

    override fun provideCoroutines(): Coroutines = TestCoroutines()

    override fun provideDatabase(context: Context): MovieDatabase {
        return Room.inMemoryDatabaseBuilder(context, MovieDatabase::class.java)
                .allowMainThreadQueries()
                .build()
    }

    override fun provideMasterWebService(context: Context): MoviesWebService {
        return FakeMasterWebService()
    }

    override fun provideDetailWebService(context: Context): MovieWebService {
        return FakeDetailWebService()
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

class FakeDetailWebService: MovieWebService {

    override fun getMovie(movieId: Int, apiKey: String, language: String): Deferred<Movie> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}

class FakeMasterWebService: MoviesWebService {

    override fun getMovies(page: Int, region: String?, apiKey: String, language: String): Deferred<Movies> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}