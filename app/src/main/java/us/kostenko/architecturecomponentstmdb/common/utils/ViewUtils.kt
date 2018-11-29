package us.kostenko.architecturecomponentstmdb.common.utils

import android.annotation.SuppressLint
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat


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

/**
 * Handy method to obtain color
 */
fun View.getColor(colorId: Int) = ContextCompat.getColor(context, colorId)

/**
 * Allows to retrieve values from styled Attributes without worrying about
 * recycling it afterwards
 */
@SuppressLint("Recycle")
inline fun View.styledAttributesTransaction(attrs: AttributeSet?, styleables: IntArray, block: TypedArray.() -> Unit) {
    context.obtainStyledAttributes(attrs, styleables).apply(block).recycle()
}

inline fun <T> ViewGroup.inflate(layout: Int, after: (View) -> T): T {
    val inflater = LayoutInflater.from(context)
    return after(inflater.inflate(layout, this, false))
}