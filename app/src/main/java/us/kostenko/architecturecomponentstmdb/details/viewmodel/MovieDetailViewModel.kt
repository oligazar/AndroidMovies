package us.kostenko.architecturecomponentstmdb.details.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import us.kostenko.architecturecomponentstmdb.common.Coroutines
import us.kostenko.architecturecomponentstmdb.details.model.Movie
import us.kostenko.architecturecomponentstmdb.details.repository.MovieDetailRepository
import us.kostenko.architecturecomponentstmdb.details.viewmodel.netres.State
import us.kostenko.architecturecomponentstmdb.testing.OpenForTesting


@OpenForTesting
class MovieDetailViewModel(
        private val coroutines: Coroutines,
        private val repo: MovieDetailRepository
): ViewModel() {

    private var _movieId = MutableLiveData<Int>()

    var movie: LiveData<State<Movie>> = Transformations.switchMap(_movieId) { input ->
                repo.getMovie(input)
            }

    fun like(id: Int, like: Boolean) {
        repo.like(id, like)
    }

    fun retry(id: Int) {
        _movieId.value = id
        repo.retry(id)
    }

    override fun onCleared() {
        coroutines.cancel() // сделать корутины lifecycle observer -ами
    }
}