package us.kostenko.architecturecomponentstmdb.master.view

import androidx.lifecycle.MutableLiveData
import androidx.paging.PagedList
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.filters.LargeTest
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import us.kostenko.architecturecomponentstmdb.R
import us.kostenko.architecturecomponentstmdb.master.model.MovieItem
import us.kostenko.architecturecomponentstmdb.tools.FragmentTestRule
import us.kostenko.architecturecomponentstmdb.tools.RecyclerViewItemCountAssertion.Companion.withItemCount
import us.kostenko.architecturecomponentstmdb.tools.check
import us.kostenko.architecturecomponentstmdb.tools.mockPagedList
import us.kostenko.architecturecomponentstmdb.tools.withItemText


@LargeTest
class MasterFragmentTest {

    private val moviesLD = MutableLiveData<PagedList<MovieItem>>()
    @get:Rule val fragmentRule = FragmentTestRule()

    @Before fun setUp() {
        val fragment = MoviesFragment()
        fragment.moviesVmFactory = { mock { on { movies } doReturn moviesLD } }
        fragment.itemVmFactory = { mock { on { showDetails } doReturn MutableLiveData() } }
        fragmentRule.launchFragment(fragment)
    }

    @Test
    fun checkIfItemsListIsDisplayed() {
        val title = "Dummy Title"
        val movies = listOf(MovieItem(1, title))
        val pagedList = mockPagedList(movies)

        moviesLD.postValue(pagedList)

        R.id.recycler check withItemCount(1)
        onView(withItemText(title)) check matches(isDisplayed())
    }
}


