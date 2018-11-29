package us.kostenko.architecturecomponentstmdb.master.view


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.android.synthetic.main.fragment_movies.recycler
import kotlinx.android.synthetic.main.fragment_movies.swipeRefresh
import kotlinx.android.synthetic.main.fragment_movies.toolbar
import us.kostenko.architecturecomponentstmdb.R
import us.kostenko.architecturecomponentstmdb.common.di.Injector
import us.kostenko.architecturecomponentstmdb.common.utils.appCompatActivity
import us.kostenko.architecturecomponentstmdb.common.utils.inTransaction
import us.kostenko.architecturecomponentstmdb.common.utils.observe
import us.kostenko.architecturecomponentstmdb.common.utils.viewModelProvider
import us.kostenko.architecturecomponentstmdb.common.view.GridItemDecorator
import us.kostenko.architecturecomponentstmdb.common.view.create
import us.kostenko.architecturecomponentstmdb.details.view.MovieDetailFragment
import us.kostenko.architecturecomponentstmdb.master.view.adapter.MoviesProgressAdapter
import us.kostenko.architecturecomponentstmdb.master.view.adapter.RecyclerState
import us.kostenko.architecturecomponentstmdb.master.viewmodel.MovieItemViewModel
import us.kostenko.architecturecomponentstmdb.master.viewmodel.MoviesViewModel

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MoviesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
class MoviesFragment : Fragment() {

    var moviesVmFactory: () -> MoviesViewModel = { Injector.moviesViewModel(requireContext()) }
    var itemVmFactory: () -> MovieItemViewModel = { Injector.itemViewModel() }
    private val moviesViewModel: MoviesViewModel by viewModelProvider { moviesVmFactory() }
    private val itemViewModel: MovieItemViewModel by viewModelProvider { itemVmFactory() }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_movies, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        appCompatActivity {
            setSupportActionBar(toolbar)
            supportActionBar?.title = getString(R.string.title_fragment)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        initAdapter()
        initSwipeToRefresh()

        itemViewModel.showDetails.observe(this, Observer { event ->
            event?.getValueIfNotHandled()?.let { movieId ->
                showDetails(movieId)
            }
        })
    }

    private fun initAdapter() {
        val movieAdapter = MoviesProgressAdapter(itemViewModel) {
            moviesViewModel.retry()
        }
        moviesViewModel.movies.observe(this) { movies ->
            movieAdapter.submitList(movies)
        }
        moviesViewModel.networkState.observe(this) {
            movieAdapter.setNetworkState(it)
        }
        val rvLayoutManager = GridLayoutManager(context, 2).apply {
            spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int =
                        when (movieAdapter.getItemViewType(position)) {
                            R.layout.item_network_state -> 2 //number of columns of the grid
                            else -> 1
                        }
            }
        }
        recycler.apply {
            adapter = movieAdapter
            layoutManager = rvLayoutManager
            addItemDecoration(GridItemDecorator(2, 8, 8, true))
            setHasFixedSize(true)
        }
    }

    private fun initSwipeToRefresh() {
        moviesViewModel.refreshState.observe(this) {
            swipeRefresh.isRefreshing = it == RecyclerState.InProgress
        }
        swipeRefresh.setOnRefreshListener {
            moviesViewModel.refresh()
        }
    }

    private fun showDetails(movieId: Int) {
        activity?.supportFragmentManager?.inTransaction {
            replace(R.id.container, MovieDetailFragment.create(movieId))
            addToBackStack(null)
        }
    }
}
