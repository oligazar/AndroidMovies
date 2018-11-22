package us.kostenko.architecturecomponentstmdb.details.repository

import androidx.lifecycle.Observer
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.nhaarman.mockitokotlin2.mock
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.dsl.module.module
import org.koin.standalone.StandAloneContext.loadKoinModules
import org.koin.standalone.StandAloneContext.stopKoin
import org.koin.standalone.inject
import org.koin.test.KoinTest
import org.mockito.ArgumentCaptor
import us.kostenko.architecturecomponentstmdb.common.database.MovieDatabase
import us.kostenko.architecturecomponentstmdb.details.model.Movie
import us.kostenko.architecturecomponentstmdb.details.repository.persistance.DetailDao
import java.util.Date


private val testModule = module(override = true) {
    single {
        Room.inMemoryDatabaseBuilder(get(), MovieDatabase::class.java)
                .allowMainThreadQueries()
                .build()
    }
}


@RunWith(AndroidJUnit4::class)
class MovieDatabaseTest: KoinTest {

//    @get:Rule
//    val instantExecutorRule = InstantTaskExecutorRule()
    val movieDatabase: MovieDatabase by inject()
    val movieDAO: DetailDao by inject()

    @Before fun before() {
        loadKoinModules(testModule)
    }

    @After fun after() {
        movieDatabase.close()
        stopKoin()
    }

    @Test
    fun saveMovieTest() {
        val observer = mock<Observer<Movie>>()
        val date = Date()
        val movie = buildMovie(1, dateUpdate = date)
        val captor = ArgumentCaptor.forClass(Movie::class.java)

        movieDAO.saveMovie(movie)
//        val movieLD = movieDAO.getMovie(1)
//        movieLD.observeForever(observer)

//        verify(observer).onChanged(captor.capture())
//        assertEquals(captor.value, buildMovie(1, dateUpdate = date))
    }

    @Test
    fun likeTest() {
        val movie = buildMovie(1)

        movieDAO.saveMovie(movie)
        movieDAO.like(1, true)
//        val movieLD = movieDAO.getMovie(1)
//        movieLD.observeForever {
//            assert(it.liked)
//        }
    }

//    @Test
//    fun getMovieByDateTest() {
//        val dateUpdate = Date()
//        val queryDate = Date(System.currentTimeMillis() - 3600 * 1000)
//        val movie = buildMovie(1, dateUpdate = dateUpdate)
//        movieDAO.saveMovie(movie)
//
//        val savedMovie = movieDAO.getMovieByDate(1, queryDate)
//
//        assertNotNull(savedMovie)
//    }
}

fun buildMovie(id: Int,
                       liked: Boolean = false,
                       sort: Int = 1,
                       prefix: String = "",
                       dateUpdate: Date = Date()) =
        Movie(id, "${prefix}Title", "${prefix}Date", "${prefix}poster", "${prefix}backdrop",
              "${prefix}overview", "${prefix}eng", "${prefix}origTitle",
              null, dateUpdate, liked, sort)

//    @Test
//    fun equalsTest() {
//        val date = Date()
//        val movie1 = buildMovie(1, prefix = "new ", dateUpdate = date)
//        val movie2 = buildMovie(1, prefix = "new ", dateUpdate = date)
//        assertEquals(movie1, movie2)
//    }

//    @Test
//    fun updateMovieTest() {
//        val dateUpdate = Date()
//        val initialMovie = buildMovie(1, true, 3)
//        val updateMovie = buildMovie(1, false, 1, "new ")
//        val resultMovie = buildMovie(1, true, 3, "new ", dateUpdate)
//        val captor = ArgumentCaptor.forClass(Movie::class.java)
//        val observer = mock<Observer<Movie>>()
//
//        val movieLD = movieDAO.getMovie(1)
//        movieLD.observeForever(observer)
//
//        movieDAO.saveMovie(initialMovie)
//        movieDAO.updateMovie(updateMovie, dateUpdate)
//
//        verify(observer, times(3)).onChanged(captor.capture())
//        assertEquals(captor.value, resultMovie)
//    }