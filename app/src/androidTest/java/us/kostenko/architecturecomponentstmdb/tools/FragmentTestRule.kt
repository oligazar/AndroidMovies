package us.kostenko.architecturecomponentstmdb.tools

import androidx.fragment.app.Fragment
import androidx.test.rule.ActivityTestRule
import us.kostenko.architecturecomponentstmdb.android.MainActivity

class FragmentTestRule : ActivityTestRule<MainActivity>(MainActivity::class.java, false, false) {

    fun launchFragment(fragment: Fragment) {
        launchActivity(null)
        activity.setFragment(fragment)
    }
}