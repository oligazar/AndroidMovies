package us.kostenko.architecturecomponentstmdb.master.view


import android.arch.paging.PageKeyedDataSource
import android.arch.paging.PagedList
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.support.v4.app.Fragment
import android.support.v7.util.DiffUtil
import android.support.v7.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_movies.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import us.kostenko.architecturecomponentstmdb.R
import us.kostenko.architecturecomponentstmdb.common.GridItemDecorator
import us.kostenko.architecturecomponentstmdb.common.api.retrofit.RetrofitManager
import us.kostenko.architecturecomponentstmdb.common.utils.appCompatActivity
import us.kostenko.architecturecomponentstmdb.common.utils.viewModelProvider
import us.kostenko.architecturecomponentstmdb.details.model.Movie
import us.kostenko.architecturecomponentstmdb.master.model.Movies
import us.kostenko.architecturecomponentstmdb.master.repository.webservice.MoviesWebService
import us.kostenko.architecturecomponentstmdb.master.view.adapter.MoviesAdapter
import us.kostenko.architecturecomponentstmdb.master.viewmodel.MoviesViewModel
import java.util.concurrent.Executor
import java.util.concurrent.Executors


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
//        viewModel.movies.observe(this, Observer {
//            Timber.d("movies: $it")
//        })
        val dataSource = MoviesDataSource(RetrofitManager.createService(activity!!.application, MoviesWebService::class.java))
        val config = PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .build()
//
        val pagedList = PagedList.Builder(dataSource, config)
                .setFetchExecutor(Executors.newSingleThreadExecutor())
                .setNotifyExecutor(MainThreadExecutor())
                .build()

        val diffUtilCallback = MovieDiffUtilCallback()

        val movieAdapter = MoviesAdapter(diffUtilCallback)
        movieAdapter.submitList(pagedList)
        recycler.apply {
            adapter = movieAdapter
            layoutManager = GridLayoutManager(activity, 2)
            addItemDecoration(GridItemDecorator(2, 8, 8,true))
            setHasFixedSize(true)
        }
    }

    internal inner class MainThreadExecutor : Executor {
        private val mHandler = Handler(Looper.getMainLooper())

        override fun execute(command: Runnable) {
            mHandler.post(command)
        }
    }

    class MoviesDataSource(private val service: MoviesWebService): PageKeyedDataSource<Int, Movie>() {

        override fun loadInitial(params: LoadInitialParams<Int>,
                                 callback: LoadInitialCallback<Int, Movie>) {
            Timber.d("loadInitial, requestedLoadSize: ${params.requestedLoadSize}, placeholdersEnabled: ${params.placeholdersEnabled}")
            service.getMovies(1).enqueue(object: Callback<Movies> {
                override fun onFailure(call: Call<Movies>, t: Throwable) {
                    Timber.d("call: $call, error: $t")
                }

                override fun onResponse(call: Call<Movies>, response: Response<Movies>) {
                    val movies = response.body()?.results
                    if (movies != null) {
                        callback.onResult(movies, null, 2)
                    } else {
                        Timber.d("call: $call, response: $response")
                    }
                }
            })
        }

        override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, Movie>) {
            Timber.d("loadAfter, key: ${params.key}, requestedLoadSize: ${params.requestedLoadSize}")
            service.getMovies(params.key).enqueue(object: Callback<Movies> {
                override fun onFailure(call: Call<Movies>, t: Throwable) {
                    Timber.d("call: $call, error: $t")
                }

                override fun onResponse(call: Call<Movies>, response: Response<Movies>) {
                    val movies = response.body()?.results
                    if (movies != null) {
                        callback.onResult(movies, params.key + 1)
                    } else {
                        Timber.d("call: $call, response: $response")
                    }
                }
            })
        }

        override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, Movie>) { }
    }

    internal inner class MovieDiffUtilCallback: DiffUtil.ItemCallback<Movie>() {
        override fun areItemsTheSame(p0: Movie, p1: Movie) = p0.id == p1.id
        override fun areContentsTheSame(p0: Movie, p1: Movie) = p0 == p1
    }
}
