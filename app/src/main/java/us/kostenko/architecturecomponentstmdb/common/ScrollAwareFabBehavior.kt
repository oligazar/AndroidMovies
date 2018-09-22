package us.kostenko.architecturecomponentstmdb.common

import android.content.Context
import android.support.design.widget.AppBarLayout
import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.FloatingActionButton
import android.util.AttributeSet
import android.view.View


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