package us.kostenko.architecturecomponentstmdb.common

import android.databinding.BindingAdapter
import android.databinding.BindingConversion
import android.widget.ImageView
import android.widget.TextView
import us.kostenko.architecturecomponentstmdb.R
import us.kostenko.architecturecomponentstmdb.common.utils.setImage
import us.kostenko.architecturecomponentstmdb.common.utils.tmdbPicPath
import us.kostenko.architecturecomponentstmdb.details.model.Genre


@BindingAdapter("app:url")
fun loadImage(iv: ImageView, path: String?) {
    iv.setImage(path?.tmdbPicPath())
}

@BindingAdapter("app:title", "app:date")
fun setTitleDate(tv: TextView, title: String?, date: String?) {
    val titleDate = if (title == null) date ?: ""
    else tv.context.getString(R.string.format_title_date, title, date)
    tv.text = titleDate
}

@BindingConversion
fun genresToString(genres: ArrayList<Genre>?): String? = genres?.joinToString { it.name.toLowerCase() }