package us.kostenko.architecturecomponentstmdb.master.view.adapter

import android.arch.paging.PagedListAdapter
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import us.kostenko.architecturecomponentstmdb.databinding.ItemMovieBinding
import us.kostenko.architecturecomponentstmdb.details.model.Movie

class MoviesAdapter(callback: DiffUtil.ItemCallback<Movie>): PagedListAdapter<Movie, MoviesViewHolder>(callback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MoviesViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemMovieBinding.inflate(inflater, parent, false)
        return MoviesViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MoviesViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class MoviesViewHolder(private val binding: ItemMovieBinding): RecyclerView.ViewHolder(binding.root) {

    fun bind(movie: Movie?) {
        binding.movie = movie
        binding.executePendingBindings()
    }
}