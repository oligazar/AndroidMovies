package us.kostenko.architecturecomponentstmdb.master.view

import androidx.lifecycle.MutableLiveData
import androidx.paging.PagedList
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.filters.MediumTest
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.koin.androidx.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module
import org.koin.standalone.StandAloneContext
import org.koin.test.KoinTest
import us.kostenko.architecturecomponentstmdb.R
import us.kostenko.architecturecomponentstmdb.details.repository.persistance.DetailDao
import us.kostenko.architecturecomponentstmdb.master.model.MovieItem
import us.kostenko.architecturecomponentstmdb.master.viewmodel.MoviesViewModel
import us.kostenko.architecturecomponentstmdb.tools.FragmentTestRule
import us.kostenko.architecturecomponentstmdb.tools.RecyclerViewItemCountAssertion.Companion.withItemCount
import us.kostenko.architecturecomponentstmdb.tools.mockPagedList
import us.kostenko.architecturecomponentstmdb.tools.withItemText

@MediumTest
class MasterFragmentTest: KoinTest {

    @get:Rule val fragmentRule = FragmentTestRule()
    private val moviesLD = MutableLiveData<PagedList<MovieItem>>()

    private val testModule = module {
        viewModel(override = true) {
            mock<MoviesViewModel> { on { movies } doReturn moviesLD }
        }
//        viewModel { mock<MovieItemViewModel> { on { showDetails } doReturn MutableLiveData() } }
        single(override = true) { mock<DetailDao>() }
    }

    @Before
    fun setUp() {
        StandAloneContext.loadKoinModules(testModule)
        fragmentRule.launchFragment(MoviesFragment())
    }

    @Test
    fun checkIfItemsListIsDisplayed() {
        val title = "Dummy Title"
        val movies = listOf(MovieItem(1, title))
        val pagedList = mockPagedList(movies)

        moviesLD.postValue(pagedList)

        onView(withId(R.id.recycler)).check(withItemCount(1))
        onView(withItemText(title)).check(matches(isDisplayed()))
    }
}