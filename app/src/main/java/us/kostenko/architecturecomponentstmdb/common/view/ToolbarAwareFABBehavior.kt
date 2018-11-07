package us.kostenko.architecturecomponentstmdb.common.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton


class ToolbarAwareFABBehavior(context: Context,
                             attrs: AttributeSet) : CoordinatorLayout.Behavior<FloatingActionButton>() {

    override fun onDependentViewChanged(parent: CoordinatorLayout, child: FloatingActionButton,
                                        dependency: View): Boolean {
        return if (dependency is AppBarLayout) {
            val threshold = dependency.height / 2
            val shift = dependency.height + dependency.y
            if (shift < threshold && child.visibility == View.VISIBLE) {
                child.hide()
            } else if (shift > threshold && child.visibility != View.VISIBLE) {
                child.show()
            }
            true
        } else false
    }

    override fun layoutDependsOn(parent: CoordinatorLayout, child: FloatingActionButton,
                                 dependency: View) = dependency is AppBarLayout
}