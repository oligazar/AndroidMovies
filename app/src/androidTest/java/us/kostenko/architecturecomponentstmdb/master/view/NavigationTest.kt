package us.kostenko.architecturecomponentstmdb.master.view

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.filters.LargeTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import us.kostenko.architecturecomponentstmdb.R
import us.kostenko.architecturecomponentstmdb.common.database.MovieDatabase
import us.kostenko.architecturecomponentstmdb.common.di.Injector
import us.kostenko.architecturecomponentstmdb.tools.FragmentTestRule
import us.kostenko.architecturecomponentstmdb.tools.check
import us.kostenko.architecturecomponentstmdb.tools.withItemText


@LargeTest
class NavigationTest {

    @get:Rule val fragmentRule = FragmentTestRule()
    @get:Rule val instantTask = InstantTaskExecutorRule()
    private val movieDatabase: MovieDatabase = Injector.provideDatabase(ApplicationProvider.getApplicationContext())

    @Before fun setup() {
        val fragment = MoviesFragment()
        fragmentRule.launchFragment(fragment)
    }

    @After fun after() {
        movieDatabase.close()
    }

    @Test
    @Throws(Exception::class)
    fun clickAddNoteButton_opensAddNoteUi() {
        val title = "Title"
        // Click on list item
        onView(withItemText(title)).perform(click())

        // Check if the movie detail screen is displayed
        R.id.tvTitle check matches(isDisplayed())
    }
}