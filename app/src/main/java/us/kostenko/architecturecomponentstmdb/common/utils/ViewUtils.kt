package us.kostenko.architecturecomponentstmdb.common.utils

import android.content.res.TypedArray
import androidx.core.content.ContextCompat
import android.util.AttributeSet
import android.view.View


/**
 * Transforms sp value to pixels
 */
fun View.sp(sp: Float) = Math.round(sp * resources.displayMetrics.scaledDensity).toFloat()

fun View.sp(sp: Int) = Math.round(sp * resources.displayMetrics.scaledDensity)
/**
 * Transforms dip value to pixels
 */
fun View.dp(sp: Float) = Math.round(sp * resources.displayMetrics.density).toFloat()

fun View.dp(sp: Int) = Math.round(sp * resources.displayMetrics.density)

fun View.visibility(isVisible: Boolean) {
    visibility = if (isVisible) View.VISIBLE else View.GONE
}

/**
 * Handy method to obtain color
 */
fun View.getColor(colorId: Int) = ContextCompat.getColor(context, colorId)

/**
 * Allows to retrieve values from styled Attributes without worrying about
 * recycling it afterwards
 */
inline fun View.styledAttributesTransaction(attrs: AttributeSet?, styleables: IntArray, block: TypedArray.() -> Unit) {
    context.obtainStyledAttributes(attrs, styleables).apply(block).recycle()
}