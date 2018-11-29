package us.kostenko.architecturecomponentstmdb.details.view

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.filters.LargeTest
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import org.hamcrest.Matchers.not
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import us.kostenko.architecturecomponentstmdb.R
import us.kostenko.architecturecomponentstmdb.common.di.buildMovie
import us.kostenko.architecturecomponentstmdb.common.view.create
import us.kostenko.architecturecomponentstmdb.details.model.Movie
import us.kostenko.architecturecomponentstmdb.details.viewmodel.netres.State
import us.kostenko.architecturecomponentstmdb.tools.FragmentTestRule
import us.kostenko.architecturecomponentstmdb.tools.check

/** in Mock flavor */
@LargeTest
class MovieDetailFragmentTest {

    private val movieLD = MutableLiveData<State<Movie>>()
    private val movie: Movie = buildMovie(1)

    @get:Rule val fragmentRule = FragmentTestRule()
    @get:Rule val instantTaskRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        val fragment = MovieDetailFragment.create(1) as MovieDetailFragment
        fragment.factory = { mock { on { movie } doReturn movieLD } }
        fragmentRule.launchFragment(fragment)
    }

    @Test fun when_InitialLoading_should_showInitialProgress() {
        movieLD.postValue(State.InitialLoading)

        R.id.initialProgress check matches(isDisplayed())
        R.id.retry check matches(not(isDisplayed()))
    }

    @Test fun when_Success_should_showMovieTitle() {
        movieLD.postValue(State.InitialLoading)
        movieLD.postValue(State.Success(movie))

        R.id.initialProgress check matches(not(isDisplayed()))
        R.id.tvTitle check matches(isDisplayed())
    }

    @Test fun when_LoadingAfterSuccess_should_showProgress() {
//        movieLD.postValue(State.InitialLoading)
//        movieLD.postValue(State.Loaded(movie))
        movieLD.postValue(State.Loading)

        R.id.progress check matches(isDisplayed())
//        R.id.tvTitle check matches(isDisplayed())
//        R.id.initialProgress check matches(not(isDisplayed()))
    }
}