package us.kostenko.architecturecomponentstmdb.details.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import us.kostenko.architecturecomponentstmdb.common.TestCoroutines
import us.kostenko.architecturecomponentstmdb.details.repository.MovieDetailRepository


@RunWith(JUnit4::class)
class MovieDetailViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val coroutines = TestCoroutines()
    private val repo: MovieDetailRepository = mock()
    private lateinit var movieDetailVM: MovieDetailViewModel

    @Before
    fun setup() {
        movieDetailVM = MovieDetailViewModel(coroutines, repo)
    }

    @Test
    fun `get movie calls repo_getMovie`() {
        movieDetailVM.movie.observeForever { }
        movieDetailVM.retry(MOVIE_ID)

        verify(repo).getMovie(MOVIE_ID)
    }

    @Test
    fun `like calls repo_like`()  {
        movieDetailVM.like(MOVIE_ID, true)

        verify(repo).like(MOVIE_ID, true)
    }

    @Test
    fun `retry calls repo_getMovie`() {
        movieDetailVM.movie.observeForever { }
        movieDetailVM.retry(MOVIE_ID)
        movieDetailVM.retry(MOVIE_ID)

        verify(repo, times(2)).getMovie(MOVIE_ID)
    }

    companion object {
        const val MOVIE_ID = 23
    }
}