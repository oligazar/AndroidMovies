package us.kostenko.architecturecomponentstmdb.details.view

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_movie_detail.*
import timber.log.Timber
import us.kostenko.architecturecomponentstmdb.R
import us.kostenko.architecturecomponentstmdb.common.FragmentCreator
import us.kostenko.architecturecomponentstmdb.common.utils.appCompatActivity
import us.kostenko.architecturecomponentstmdb.common.utils.setImage
import us.kostenko.architecturecomponentstmdb.common.utils.tmdbPicPath
import us.kostenko.architecturecomponentstmdb.details.viewmodel.MovieDetailViewModel

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link KotlinFragment#newInstance} factory method to
 * create an instance of this fragment.
 * lyfecycle owner for fragment:  https://medium.com/@BladeCoder/architecture-components-pitfalls-part-1-9300dd969808
 */
class MovieDetailFragment: Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return  inflater.inflate(R.layout.fragment_movie_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        appCompatActivity {
            setSupportActionBar(toolbar)
            supportActionBar?.setDisplayHomeAsUpEnabled(false)
            supportActionBar?.title = null
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val viewModel = ViewModelProviders.of(this).get(MovieDetailViewModel::class.java)
        viewModel.init(param)
        Timber.d("Just test")
        viewModel.movie.observe(this, Observer { movie ->
            tvTitle.text = movie?.title ?: ""
            tvOrigTitleReleaseDate.text = getOrigTitleDate(movie?.original_title, movie?.release_date)
            toolbarImage.setImage(movie?.poster_path?.tmdbPicPath())
            tvGenres.text = movie?.genres?.joinToString { it.name.toLowerCase() }
            movieTitle.text = movie?.title
            movieDescription.text = movie?.overview
            Timber.d("movie: $movie")
        })
    }

    private fun getOrigTitleDate(title: String?, date: String?): String {
        return if (title != null) getString(R.string.format_title_date, title, date)
        else date ?: ""
    }

    companion object: FragmentCreator<Int>(::MovieDetailFragment)
}
