package us.kostenko.architecturecomponentstmdb.master.view.adapter

import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import us.kostenko.architecturecomponentstmdb.databinding.ItemMovieBinding
import us.kostenko.architecturecomponentstmdb.master.model.MovieItem
import us.kostenko.architecturecomponentstmdb.master.viewmodel.MovieItemViewModel

class MoviesAdapter(callback: DiffUtil.ItemCallback<MovieItem>, private val vm: MovieItemViewModel): PagedListAdapter<MovieItem, MoviesViewHolder>(callback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MoviesViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemMovieBinding.inflate(inflater, parent, false)
        return MoviesViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MoviesViewHolder, position: Int) {
        holder.bind(getItem(position), vm)
    }
}

class MoviesViewHolder(private val binding: ItemMovieBinding): RecyclerView.ViewHolder(binding.root) {

    fun bind(movie: MovieItem?, vm: MovieItemViewModel) {
//        binding.root.setOnClickListener { movie?.id?.let { id -> vm.showDetails(id) } }
        binding.viewModel = vm
        binding.movie = movie
        binding.executePendingBindings()
    }
}