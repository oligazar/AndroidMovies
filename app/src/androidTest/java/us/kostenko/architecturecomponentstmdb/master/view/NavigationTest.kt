package us.kostenko.architecturecomponentstmdb.master.view

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.koin.dsl.module.module
import org.koin.standalone.StandAloneContext.loadKoinModules
import us.kostenko.architecturecomponentstmdb.R
import us.kostenko.architecturecomponentstmdb.common.AndroidCoroutines
import us.kostenko.architecturecomponentstmdb.common.Coroutines
import us.kostenko.architecturecomponentstmdb.common.database.MovieDatabase
import us.kostenko.architecturecomponentstmdb.details.repository.buildMovie
import us.kostenko.architecturecomponentstmdb.master.model.Dates
import us.kostenko.architecturecomponentstmdb.master.model.Movies
import us.kostenko.architecturecomponentstmdb.master.repository.webservice.MoviesWebService
import us.kostenko.architecturecomponentstmdb.tools.FragmentTestRule
import us.kostenko.architecturecomponentstmdb.tools.withItemText

class NavigationTest {

    @get:Rule val fragmentRule = FragmentTestRule()
    @get:Rule val instantTask = InstantTaskExecutorRule()

    @Before
    fun setup() {
        loadKoinModules(testModule)
        fragmentRule.launchFragment(MoviesFragment())
    }

    @Test
    @Throws(Exception::class)
    fun clickAddNoteButton_opensAddNoteUi() {
        val title = "Title"
        // Click on list item
        onView(withItemText(title)).perform(click())

        // Check if the movie detail screen is displayed
        onView(ViewMatchers.withId(R.id.tvTitle)).check(matches(isDisplayed()))
    }

    private val testModule = module(override = true) {
        single<Coroutines> { AndroidCoroutines() }
        single { mock<MoviesWebService> {
            val items = arrayListOf(buildMovie(1))
            val movies = Movies(items, 1, 2, Dates("max", "min"), 1)
            on { getMovies(1) } doReturn GlobalScope.async { movies }
        }
        }
        single {
            Room.inMemoryDatabaseBuilder(get(), MovieDatabase::class.java)
                    .allowMainThreadQueries()
                    .build()
        }
    }
}