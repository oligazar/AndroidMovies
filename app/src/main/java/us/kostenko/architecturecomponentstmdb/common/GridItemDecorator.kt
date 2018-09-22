package us.kostenko.architecturecomponentstmdb.common

import android.content.Context
import android.graphics.Rect
import android.support.v7.widget.RecyclerView
import android.view.View
import timber.log.Timber


class GridItemDecorator(private val gridSize: Int,
                        private val spacingDp: Int = 0,
                        private val top: Int = 0,
                        private val sides: Boolean = false) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {

        val context = parent.context
        val spacingPx = context.dp(spacingDp.toFloat())

        val bit = if (spacingPx > gridSize || spacingPx == 0f) Math.round(spacingPx / gridSize) else 1
        val itemPosition = (view.layoutParams as RecyclerView.LayoutParams).viewAdapterPosition

        outRect.top = if (itemPosition < gridSize) context.dp(top) else  bit * gridSize
        Timber.d("childs: ${parent.childCount}")

        val rowPosition = itemPosition % gridSize
        if (sides) {
            outRect.left = (gridSize * bit) - (rowPosition * bit)
            outRect.right = (rowPosition + 1) * bit
        } else {
            outRect.left = rowPosition * bit
            outRect.right = (gridSize - rowPosition - 1) * bit
        }
    }

    private fun Context.dp(dp: Int) = Math.round(dp * resources.displayMetrics.density)
    private fun Context.dp(dp: Float) = Math.round(dp * resources.displayMetrics.density).toFloat()
}