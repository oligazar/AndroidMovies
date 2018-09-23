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
import us.kostenko.architecturecomponentstmdb.common.GridItemDecorator
import us.kostenko.architecturecomponentstmdb.common.utils.appCompatActivity
import us.kostenko.architecturecomponentstmdb.common.utils.viewModelProvider
import us.kostenko.architecturecomponentstmdb.details.model.Movie
import us.kostenko.architecturecomponentstmdb.master.view.adapter.MoviesAdapter
import us.kostenko.architecturecomponentstmdb.master.viewmodel.MoviesViewModel


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MoviesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
class MoviesFragment : Fragment() {
    private val viewModel by viewModelProvider {
        MoviesViewModel(activity!!.application)
    }

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

        val movieAdapter = MoviesAdapter(MovieDiffUtilCallback())
        viewModel.movies.observe(this, Observer { movies ->
            movieAdapter.submitList(movies)
        })
        recycler.apply {
            adapter = movieAdapter
            layoutManager = GridLayoutManager(activity, 2)
            addItemDecoration(GridItemDecorator(2, 8, 8,true))
            setHasFixedSize(true)
        }
    }

    internal inner class MovieDiffUtilCallback: DiffUtil.ItemCallback<Movie>() {
        override fun areItemsTheSame(p0: Movie, p1: Movie) = p0.id == p1.id
        override fun areContentsTheSame(p0: Movie, p1: Movie) = p0 == p1
    }
}
