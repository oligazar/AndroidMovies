package us.kostenko.architecturecomponentstmdb.master.view


import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.util.DiffUtil
import android.support.v7.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_movies.*
import us.kostenko.architecturecomponentstmdb.R
import us.kostenko.architecturecomponentstmdb.common.utils.appCompatActivity
import us.kostenko.architecturecomponentstmdb.common.utils.inTransaction
import us.kostenko.architecturecomponentstmdb.common.utils.viewModelProvider
import us.kostenko.architecturecomponentstmdb.common.view.GridItemDecorator
import us.kostenko.architecturecomponentstmdb.common.view.create
import us.kostenko.architecturecomponentstmdb.details.view.MovieDetailFragment
import us.kostenko.architecturecomponentstmdb.master.model.MovieItem
import us.kostenko.architecturecomponentstmdb.master.view.adapter.MoviesAdapter
import us.kostenko.architecturecomponentstmdb.master.viewmodel.MovieItemViewModel
import us.kostenko.architecturecomponentstmdb.master.viewmodel.MoviesViewModel


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MoviesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
class MoviesFragment : Fragment() {

    private val viewModel by viewModelProvider { MoviesViewModel(activity!!.application) }
    private val itemViewModel by viewModelProvider { MovieItemViewModel() }

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

        val movieAdapter = MoviesAdapter(MovieDiffUtilCallback(), itemViewModel)
        viewModel.movies.observe(this, Observer { movies ->
            movieAdapter.submitList(movies)
        })
        recycler.apply {
            adapter = movieAdapter
            layoutManager = GridLayoutManager(activity, 2)
            addItemDecoration(GridItemDecorator(2, 8, 8, true))
            setHasFixedSize(true)
        }
        itemViewModel.showDetails.observe(this, Observer { event ->
            event?.getValueIfNotHandled()?.let { movieId ->
                showDetails(movieId)
            }
        })
    }

    private fun showDetails(movieId: Int) {
        activity?.supportFragmentManager?.inTransaction {
            replace(R.id.container, MovieDetailFragment.create(movieId))
            addToBackStack(null)
        }
    }

    internal inner class MovieDiffUtilCallback: DiffUtil.ItemCallback<MovieItem>() {
        override fun areItemsTheSame(p0: MovieItem, p1: MovieItem) = p0.id == p1.id
        override fun areContentsTheSame(p0: MovieItem, p1: MovieItem) = p0 == p1
    }
}
