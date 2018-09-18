package us.kostenko.architecturecomponentstmdb.common.utils

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.DialogInterface
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import okhttp3.Cache
import us.kostenko.architecturecomponentstmdb.R

// obtain activity from context
private fun Context.getActivity(): Activity? {
    var c = this

    while (c is ContextWrapper) {
        if (c is Activity) return c
        c = c.baseContext
    }
    return null
}

// works from everywhere
fun Context.hideKeyboardFrom(view: View) {
    val imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(view.windowToken, 0)
}

fun Context.showAlert(title: Int, message: Int = -1, okAction: (() -> Unit)? = null, cancelAction: ((DialogInterface, Int) -> Unit)? = null) {
    AlertDialog.Builder(this)
            .setTitle(title)
            .apply{ if (message >= 0) setMessage(message ) }
            .setPositiveButton(R.string.btn_ok) { _, _ -> okAction?.invoke() }
            .apply { cancelAction?.let { setNegativeButton(R.string.btn_ok, it)  } }
            .create()
            .show()
}

// only works when called from Activity
fun Activity.hideKeyboard() {
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    //Find the currently focused view, so we can grab the correct window token from it.
    var view = currentFocus
    //If no view currently has focus, create a new one, just so we can grab a window token from it
    if (view == null) {
        view = View(this)
    }
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}

fun Activity.showSoftKeyboard(view: View) {
    if (view.requestFocus()) {
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val isShowing = inputMethodManager.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
        if (!isShowing) {
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
        }
    }
}

inline fun FragmentManager.inTransaction(func: FragmentTransaction.() -> FragmentTransaction) {
    beginTransaction().func().commit()
}

// Allows to omit smth like (activity as? AppCompatActivity)?.setSupportActionbar(toolbar)
// instead appCompatActivity { setSupportActionBar(toolbar) }
inline fun Fragment.appCompatActivity(body: AppCompatActivity.() -> Unit) {
    (activity as? AppCompatActivity)?.body()
}

/**
 * Get cache from context for OkHttpClient
 */
fun Context.getCache(sizeMb: Int = 10): Cache {
    val cacheSize = sizeMb * 1024 * 1024 // 10 MiB
    return Cache(cacheDir, cacheSize.toLong())
}