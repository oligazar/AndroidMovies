package us.kostenko.architecturecomponentstmdb.master.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import us.kostenko.architecturecomponentstmdb.R
import us.kostenko.architecturecomponentstmdb.common.utils.inflate
import us.kostenko.architecturecomponentstmdb.databinding.ItemMovieBinding
import us.kostenko.architecturecomponentstmdb.master.model.MovieItem
import us.kostenko.architecturecomponentstmdb.master.view.adapter.RecyclerState.Loaded
import us.kostenko.architecturecomponentstmdb.master.viewmodel.MovieItemViewModel

sealed class RecyclerState {

    object InProgress: RecyclerState()

    object Loaded: RecyclerState()

    data class Failed(val msg: String?): RecyclerState()
}

class MoviesProgressAdapter(private val vm: MovieItemViewModel,
                            private val retryCallback: () -> Unit): PagedListAdapter<MovieItem, RecyclerView.ViewHolder>(MOVIE_DIFF_UTIL_CALLBACK) {

    private var networkState: RecyclerState? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemMovieBinding.inflate(inflater, parent, false)
        return when (viewType) {
            R.layout.item_movie -> MoviesViewHolder(binding)
            R.layout.item_network_state -> parent.inflate(viewType) { NetworkStateViewHolder(it, retryCallback) }
            else -> throw IllegalArgumentException("unknown view type $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            R.layout.item_movie -> (holder as MoviesViewHolder).bind(getItem(position), vm)
            R.layout.item_network_state-> (holder as NetworkStateViewHolder).bindTo(networkState)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int, payloads: MutableList<Any>) {
        if (payloads.isNotEmpty()) {
            val item = getItem(position)
            (holder as MoviesViewHolder).updateTitle(item)
        } else {
            onBindViewHolder(holder, position)
        }
    }

    override fun getItemCount()= super.getItemCount() + if (hasExtraRow()) 1 else 0

    fun setNetworkState(newNetworkState: RecyclerState?) {
        val previousState = this.networkState
        val hadExtraRow = hasExtraRow()
        this.networkState = newNetworkState
        val hasExtraRow = hasExtraRow()
        if (hadExtraRow != hasExtraRow) {
            if (hadExtraRow) {
                notifyItemRemoved(super.getItemCount())
            } else {
                notifyItemInserted(super.getItemCount())
            }
        } else if (hasExtraRow && previousState != newNetworkState) {
            notifyItemChanged(itemCount - 1)
        }
    }

    private fun hasExtraRow() = networkState != null && networkState != Loaded

    override fun getItemViewType(position: Int): Int {
        return when (hasExtraRow() && position == itemCount - 1) {
            true -> R.layout.item_network_state
            false -> R.layout.item_movie
        }
    }

    companion object {
        val MOVIE_DIFF_UTIL_CALLBACK = object: DiffUtil.ItemCallback<MovieItem>() {
            override fun areItemsTheSame(p0: MovieItem, p1: MovieItem) = p0.id == p1.id
            override fun areContentsTheSame(p0: MovieItem, p1: MovieItem) = p0 == p1
        }
    }
}