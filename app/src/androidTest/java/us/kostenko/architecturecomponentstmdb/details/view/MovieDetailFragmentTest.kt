package us.kostenko.architecturecomponentstmdb.details.view

import androidx.lifecycle.MutableLiveData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
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


@LargeTest
class MovieDetailFragmentTest {

    private val movieLD = MutableLiveData<State<Movie>>()
    private val movie: Movie = buildMovie(1)

    @get:Rule val fragmentRule = FragmentTestRule()

    @Before
    fun setUp() {
        val fragment = MovieDetailFragment.create(1) as MovieDetailFragment
        fragment.factory = { mock { on { movie } doReturn movieLD } }
        fragmentRule.launchFragment(fragment)
    }

    @Test fun testLoadingFragment() {
        movieLD.postValue(State.InitialLoading)

        onView(withId(R.id.initialProgress)).check(matches(isDisplayed()))
        onView(withId(R.id.retry)).check(matches(not(isDisplayed())))
    }

    @Test fun testValueWhileLoading() {

        movieLD.postValue(State.InitialLoading)
        movieLD.postValue(State.Success(movie))

        onView(withId(R.id.initialProgress)).check(matches(not(isDisplayed())))
        onView(withId(R.id.tvTitle)).check(matches(isDisplayed()))
    }
}