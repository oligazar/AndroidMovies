package us.kostenko.architecturecomponentstmdb.master.view.adapter

import androidx.recyclerview.widget.RecyclerView
import us.kostenko.architecturecomponentstmdb.databinding.ItemMovieBinding
import us.kostenko.architecturecomponentstmdb.master.model.MovieItem
import us.kostenko.architecturecomponentstmdb.master.viewmodel.MovieItemViewModel

class MoviesViewHolder(private val binding: ItemMovieBinding): RecyclerView.ViewHolder(binding.root) {

    fun bind(movie: MovieItem?, vm: MovieItemViewModel) {
//        binding.root.setOnClickListener { movie?.id?.let { id -> vm.showDetails(id) } }
        binding.viewModel = vm
        binding.movie = movie
        binding.executePendingBindings()
    }

    fun updateTitle(movie: MovieItem?) {
        binding.movie = movie
    }
}