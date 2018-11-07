package us.kostenko.architecturecomponentstmdb.details.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import us.kostenko.architecturecomponentstmdb.common.Coroutines
import us.kostenko.architecturecomponentstmdb.details.model.Movie
import us.kostenko.architecturecomponentstmdb.details.repository.MovieDetailRepository
import us.kostenko.architecturecomponentstmdb.details.viewmodel.netres.State


class MovieDetailViewModel(
        private val coroutines: Coroutines,
        private val repo: MovieDetailRepository,
        id: Int = 0
): ViewModel() {

    private var _movieId = MutableLiveData<Int>().apply { value = id }

    var movie: LiveData<State<Movie>> = Transformations.switchMap(_movieId) { input ->
                repo.getMovie(input)
            }

    fun like(id: Int, like: Boolean) {
        repo.like(id, like)
    }

    fun retry() {
        _movieId.value = _movieId.value
    }

    override fun onCleared() {
        coroutines.cancel() // сделать корутины lifecycle observer -ами
    }
}