package us.kostenko.architecturecomponentstmdb.common.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.view.children
import androidx.core.view.isVisible
import us.kostenko.architecturecomponentstmdb.R

class StateContainer: FrameLayout {

    private var loading: View
    private var error: View
    private var errorMessage: TextView
    private var retry: View

    fun showLoading() {
        children.forEach { it.isVisible = it == loading }
    }
    fun showSuccess() {
        children.forEach { it.isVisible = it != loading && it != error }
    }
    fun showError(message: String) {
        children.forEach { it.isVisible = it == error }
        errorMessage.text = message
    }

    constructor(context: Context) : super(context)

    constructor(context: Context, retryAction: () -> Unit) : super(context) {
        retry.setOnClickListener { retryAction() }
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs, 0)

    init {
        val inflater = LayoutInflater.from(context)
        loading = inflater.inflate(R.layout.loading, this, false).also { addView(it) }
        loading.isVisible = false
        error = inflater.inflate(R.layout.error, this, false).also { addView(it) }
        errorMessage = error.findViewById(R.id.error_message)
        retry = error.findViewById(R.id.retry)
    }
}