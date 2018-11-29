package us.kostenko.architecturecomponentstmdb.master.view.adapter

import android.view.View
import android.view.ViewGroup
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_network_state.view.statusErrorTv
import kotlinx.android.synthetic.main.item_network_state.view.statusProgress
import kotlinx.android.synthetic.main.item_network_state.view.statusRetryBtn

class NetworkStateViewHolder(itemView: View,
                             retryCallback: () -> Unit): RecyclerView.ViewHolder(itemView) {

    init {
        itemView.statusRetryBtn.setOnClickListener { retryCallback() }
    }

    infix fun bindTo(networkState: RecyclerState?) = with(itemView as ViewGroup) {
        when (networkState) {
            is RecyclerState.InProgress -> children.forEach { it.isVisible = it == statusProgress }
            is RecyclerState.Loaded, null -> children.forEach { it.isVisible = false }
            is RecyclerState.Failed -> {
                statusProgress.isVisible = false
                statusRetryBtn.isVisible = true
                networkState.msg?.let {
                    statusErrorTv.isVisible = true
                    statusErrorTv.text = networkState.msg
                }
            }
        }
    }
}