package us.kostenko.architecturecomponentstmdb.details.view

import androidx.lifecycle.MutableLiveData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import org.hamcrest.Matchers.not
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.koin.androidx.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module
import org.koin.standalone.StandAloneContext.loadKoinModules
import org.koin.test.AutoCloseKoinTest
import us.kostenko.architecturecomponentstmdb.R
import us.kostenko.architecturecomponentstmdb.common.view.create
import us.kostenko.architecturecomponentstmdb.details.model.Movie
import us.kostenko.architecturecomponentstmdb.details.repository.buildMovie
import us.kostenko.architecturecomponentstmdb.details.repository.persistance.DetailDao
import us.kostenko.architecturecomponentstmdb.details.viewmodel.MovieDetailViewModel
import us.kostenko.architecturecomponentstmdb.details.viewmodel.netres.State
import us.kostenko.architecturecomponentstmdb.master.viewmodel.MovieItemViewModel
import us.kostenko.architecturecomponentstmdb.master.viewmodel.MoviesViewModel
import us.kostenko.architecturecomponentstmdb.tools.FragmentTestRule

class MovieDetailFragmentTest: AutoCloseKoinTest() {

    private val movieLD = MutableLiveData<State<Movie>>()
    private val movie: Movie = buildMovie(1)

    @get:Rule val fragmentRule = FragmentTestRule()

    private val testModule = module(override = true) {
        viewModel {
            mock<MovieDetailViewModel> { on { movie } doReturn movieLD }
        }
        viewModel { mock<MovieItemViewModel> { on { showDetails } doReturn MutableLiveData() } }
        viewModel { mock<MoviesViewModel> { on { movies } doReturn MutableLiveData() } }
        single { mock<DetailDao>() }
    }

    @Before
    fun setUp() {
        loadKoinModules(testModule)
        fragmentRule.launchFragment(MovieDetailFragment.create(1))
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