package us.kostenko.architecturecomponentstmdb.common.utils

import android.widget.ImageView
import com.squareup.picasso.Picasso


// allows to switch image loading library in one place
fun ImageView.setImage(path: String?) {
    Picasso.get()
            .load(path)
            .into(this)
}