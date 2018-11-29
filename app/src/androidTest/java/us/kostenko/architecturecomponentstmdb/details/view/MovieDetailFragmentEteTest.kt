package us.kostenko.architecturecomponentstmdb.details.view

import androidx.lifecycle.MutableLiveData
import androidx.test.core.app.ApplicationProvider
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import us.kostenko.architecturecomponentstmdb.common.di.Injector
import us.kostenko.architecturecomponentstmdb.common.di.buildMovie
import us.kostenko.architecturecomponentstmdb.common.view.create
import us.kostenko.architecturecomponentstmdb.details.model.Movie
import us.kostenko.architecturecomponentstmdb.details.viewmodel.netres.State
import us.kostenko.architecturecomponentstmdb.tools.FragmentTestRule

class MovieDetailFragmentEteTest {

    private val movieLD = MutableLiveData<State<Movie>>()
    private val movie: Movie = buildMovie(1)

    @get:Rule
    val fragmentRule = FragmentTestRule()

    @Before
    fun setUp() {
        val fragment = MovieDetailFragment.create(1) as MovieDetailFragment
        val movieVM = Injector.movieDetailViewModel(ApplicationProvider.getApplicationContext(), 0)
        fragment.factory = { movieVM }
        fragmentRule.launchFragment(fragment)
    }

    @Test
    fun ete_initialLoading_success_loading_success() {

    }

    @Test
    fun ete_initialLoading_success_loading_error() {

    }
}