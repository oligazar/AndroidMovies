package us.kostenko.architecturecomponentstmdb.common.di

import android.content.Context
import androidx.room.Room
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import us.kostenko.architecturecomponentstmdb.common.Coroutines
import us.kostenko.architecturecomponentstmdb.common.TestCoroutines
import us.kostenko.architecturecomponentstmdb.common.database.MovieDatabase
import us.kostenko.architecturecomponentstmdb.details.model.Movie
import us.kostenko.architecturecomponentstmdb.details.repository.webservice.MovieWebService
import us.kostenko.architecturecomponentstmdb.master.model.Dates
import us.kostenko.architecturecomponentstmdb.master.model.Movies
import us.kostenko.architecturecomponentstmdb.master.repository.webservice.MoviesWebService


/**
 * Injections for mock build variant
 */

object Injector: Injection() {

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
}

class FakeDetailWebService: MovieWebService {

    override fun getMovie(movieId: Int, apiKey: String, language: String): Deferred<Movie> {
        return GlobalScope.async { buildMovie(1) }
    }
}

class FakeMasterWebService: MoviesWebService {

    override fun getMovies(page: Int, region: String?, apiKey: String, language: String): Deferred<Movies> {
        val items = arrayListOf(buildMovie(1))
        val movies = Movies(items, 1, 2, Dates("max", "min"), 1)
        return GlobalScope.async { movies }
    }
}