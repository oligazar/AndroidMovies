package us.kostenko.architecturecomponentstmdb.details.view

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_movie_detail.*
import timber.log.Timber
import us.kostenko.architecturecomponentstmdb.R
import us.kostenko.architecturecomponentstmdb.common.utils.appCompatActivity
import us.kostenko.architecturecomponentstmdb.common.utils.viewModelFactory
import us.kostenko.architecturecomponentstmdb.common.view.FragmentCreator
import us.kostenko.architecturecomponentstmdb.databinding.FragmentMovieDetailBinding
import us.kostenko.architecturecomponentstmdb.details.viewmodel.MovieDetailViewModel




/**
 * A simple {@link Fragment} subclass.
 * Use the {@link KotlinFragment#newInstance} factory method to
 * create an instance of this fragment.
 * lyfecycle owner for fragment:  https://medium.com/@BladeCoder/architecture-components-pitfalls-part-1-9300dd969808
 */
class MovieDetailFragment: Fragment() {

    private lateinit var binding: FragmentMovieDetailBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_movie_detail, container, false)!!
        return  binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        appCompatActivity {
            setSupportActionBar(toolbar)
            supportActionBar?.setDisplayHomeAsUpEnabled(false)
            supportActionBar?.title = null
            supportActionBar?.setDisplayShowTitleEnabled(false)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val viewModel = ViewModelProviders
                .of(this, viewModelFactory { MovieDetailViewModel(activity!!.application, param) })
                .get(MovieDetailViewModel::class.java)

        binding.viewModel = viewModel
        viewModel.movie.observe(this, Observer { movie ->
            binding.movie = movie
            Timber.d("movie: $movie")
        })
    }


    companion object: FragmentCreator<Int>(::MovieDetailFragment)
}