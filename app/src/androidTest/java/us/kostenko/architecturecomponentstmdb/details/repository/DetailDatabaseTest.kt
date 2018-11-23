package us.kostenko.architecturecomponentstmdb.details.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor
import us.kostenko.architecturecomponentstmdb.common.database.MovieDatabase
import us.kostenko.architecturecomponentstmdb.common.di.Injector
import us.kostenko.architecturecomponentstmdb.common.di.buildMovie
import us.kostenko.architecturecomponentstmdb.details.model.Movie
import us.kostenko.architecturecomponentstmdb.details.repository.persistance.DetailDao
import java.util.Date

/* Mocked database is used */
@RunWith(AndroidJUnit4::class)
class DetailDatabaseTest {

    @get:Rule val instantExecutorRule = InstantTaskExecutorRule()
    private val movieDatabase: MovieDatabase = Injector.provideDatabase(getApplicationContext())
    private val detailDAO: DetailDao = movieDatabase.detailDao()

    @After fun after() {
        movieDatabase.close()
    }

    @Test
    fun saveMovieTest() {
        val observer = mock<Observer<Movie>>()
        val date = Date()
        val movie = buildMovie(1, dateUpdate = date)
        val captor = ArgumentCaptor.forClass(Movie::class.java)

        detailDAO.saveMovie(movie)
        val movieLD = detailDAO.getMovie(1)
        movieLD.observeForever(observer)

        verify(observer).onChanged(captor.capture())
        assertEquals(buildMovie(1, dateUpdate = date), captor.value)
    }

    @Test
    fun likeTest() {
        val movie = buildMovie(1)

        detailDAO.saveMovie(movie)
        detailDAO.like(1, true)
        val movieLD = detailDAO.getMovie(1)
        movieLD.observeForever {
            assert(it.liked)
        }
    }

    @Test
    fun getMovieByDateTest() {
        val dateUpdate = Date()
        val queryDate = Date(System.currentTimeMillis() - 3600 * 1000)
        val movie = buildMovie(1, dateUpdate = dateUpdate)
        detailDAO.saveMovie(movie)

        val savedMovie = detailDAO.getMovieByDate(1, queryDate)

        assertNotNull(savedMovie)
    }

    @Test
    fun updateDetailTest() {
        val dateUpdate = Date()
        val initialMovie = buildMovie(1, true, 3)
        val updateMovie = buildMovie(1, false, 1, "new ")
        val resultMovie = buildMovie(1, true, 3, "new ", dateUpdate)
        val captor = ArgumentCaptor.forClass(Movie::class.java)
        val observer = mock<Observer<Movie>>()

        val movieLD = detailDAO.getMovie(1)
        movieLD.observeForever(observer)

        detailDAO.saveMovie(initialMovie)
        detailDAO.updateMovie(updateMovie, dateUpdate)

        verify(observer, times(3)).onChanged(captor.capture())
        assertEquals(captor.value, resultMovie)
    }
}
