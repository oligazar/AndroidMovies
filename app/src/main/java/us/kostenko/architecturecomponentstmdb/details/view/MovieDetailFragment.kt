package us.kostenko.architecturecomponentstmdb.details.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.fragment_movie_detail.progress
import kotlinx.android.synthetic.main.fragment_movie_detail.toolbar
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber
import us.kostenko.architecturecomponentstmdb.R
import us.kostenko.architecturecomponentstmdb.common.utils.appCompatActivity
import us.kostenko.architecturecomponentstmdb.common.utils.visibility
import us.kostenko.architecturecomponentstmdb.common.view.FragmentCreator
import us.kostenko.architecturecomponentstmdb.common.view.StateContainer
import us.kostenko.architecturecomponentstmdb.databinding.FragmentMovieDetailBinding
import us.kostenko.architecturecomponentstmdb.details.model.Movie
import us.kostenko.architecturecomponentstmdb.details.viewmodel.MovieDetailViewModel
import us.kostenko.architecturecomponentstmdb.details.viewmodel.netres.State


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link KotlinFragment#newInstance} factory method to
 * create an instance of this fragment.
 * lyfecycle owner for fragment:  https://medium.com/@BladeCoder/architecture-components-pitfalls-part-1-9300dd969808
 */
class MovieDetailFragment: Fragment() {

    private lateinit var binding: FragmentMovieDetailBinding
    private lateinit var stateContainer: StateContainer

    private val viewModel: MovieDetailViewModel by viewModel()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        stateContainer = StateContainer(requireContext(), ::retry)
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_movie_detail, stateContainer, true)
        return  stateContainer
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

        retry()

        binding.viewModel = viewModel
        viewModel.movie.observe(this, Observer { state ->
            handleState(state)
            Timber.d("state: $state")
        })
    }

    private fun handleState(state: State<Movie>?) {
        when (state) {
            is State.InitialLoading -> {
                stateContainer.showLoading()
            }
            is State.Retry -> {
                stateContainer.showError(state.message ?: "Unknown error")
            }
            is State.Success -> {
                stateContainer.showSuccess()
                binding.movie = state.data
                progress.visibility(false)
            }
            is State.Loading -> {
                progress.visibility(true)
            }
            is State.Error -> {
                progress.visibility(false)
                Timber.d("$state.message")
                state.message.getValueIfNotHandled()?.let { showError(it) }
            }
        }
    }

    private fun retry() {
        Timber.d("Retry clicked")
            viewModel.retry(param)
    }

    private fun showError(message: String) {
        AlertDialog.Builder(requireContext(), R.style.MyAlertDialogStyle)
                .setTitle(R.string.dialog_title_error)
                .setMessage(message)
                .setPositiveButton(R.string.dialog_btn_retry) { _, _ ->
                    retry()
                    Timber.d("btn reload clicked")
                }
                .setNegativeButton(R.string.dialog_btn_close) { _, _ ->
                    Timber.d("btn close clicked")
                }
                .create()
//                    .apply {
//                        setOnShowListener {
//                            val b = getButton(DialogInterface.BUTTON_POSITIVE)
//                            b.setTextColor(ContextCompat.getColor(context, R.color.colorOk))
//                        }
//                    }
                .show()
    }

    companion object: FragmentCreator<Int>(::MovieDetailFragment)
}