package us.kostenko.architecturecomponentstmdb.master.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import androidx.test.core.app.ApplicationProvider
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import org.junit.After
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import us.kostenko.architecturecomponentstmdb.common.database.MovieDatabase
import us.kostenko.architecturecomponentstmdb.common.di.Injector
import us.kostenko.architecturecomponentstmdb.common.di.buildMovie
import us.kostenko.architecturecomponentstmdb.details.model.Movie
import us.kostenko.architecturecomponentstmdb.details.repository.persistance.DetailDao
import us.kostenko.architecturecomponentstmdb.master.model.MovieItem
import us.kostenko.architecturecomponentstmdb.master.repository.persistance.MasterDao
import java.util.Date

class MasterDatabaseTest {

    @get:Rule val instantExecutorRule = InstantTaskExecutorRule()
    private val movieDatabase: MovieDatabase = Injector.provideDatabase(ApplicationProvider.getApplicationContext())
    private val masterDAO: MasterDao = movieDatabase.masterDao()
    private val detailDAO: DetailDao = movieDatabase.detailDao()

    @After fun after() {
        movieDatabase.close()
    }

    @Test
    fun saveMasterTest() {
        val observer = mock<Observer<PagedList<MovieItem>>>()
        val movie = buildMovie(1)
        val captor = argumentCaptor<PagedList<MovieItem>>()

        masterDAO.saveMaster(movie)
        val dataSourceFactory = masterDAO.getMovies()
        val pagedList = LivePagedListBuilder(dataSourceFactory, 20).build()
        pagedList.observeForever(observer)

        verify(observer).onChanged(captor.capture())
        Assert.assertEquals(listOf(buildMovieItem(1)), captor.lastValue)
    }

    @Test
    fun updateMasterTest() {
        val dateUpdate = Date()
        val initialMovie = buildMovie(1, true, 3)
        val updateMovie = buildMovie(1, false, 1, "new ")
        val resultMovieItem = MovieItem(1, "new Title", "new poster")

        val captor = argumentCaptor<PagedList<MovieItem>>()
        val observer = mock<Observer<PagedList<MovieItem>>>()

        val dataSourceFactory = masterDAO.getMovies()
        val pagedList = LivePagedListBuilder(dataSourceFactory, 20).build()
        pagedList.observeForever(observer)

        masterDAO.saveMaster(initialMovie)
        masterDAO.updateMaster(updateMovie, dateUpdate)

        verify(observer, times(3)).onChanged(captor.capture())
        Assert.assertEquals(captor.lastValue, listOf(resultMovieItem))
    }

    @Test
    fun saveMoviesNotUpdateLiked() {
        val dateUpdate = Date()
        val initialMovies = (1..3).map { buildMovie(it, true, it) }
        val updateMovies = (1..3).map { buildMovie(it, false, it + 1, "update ", dateUpdate) }
        val resultMovies = (1..3).map { buildMovie(it, true, it + 1, "update ", dateUpdate) }

        masterDAO.saveMovies(ArrayList(initialMovies))
        masterDAO.saveMovies(ArrayList(updateMovies), dateUpdate)

        val result = mutableListOf<Movie>()
        (1..3).forEach { detailDAO.getMovie(it).observeForever { movie -> result += movie } }

        Assert.assertEquals(result, resultMovies)
    }

    private fun buildMovieItem(id: Int) = MovieItem(id, "Title", "poster")
}

