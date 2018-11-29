package us.kostenko.architecturecomponentstmdb.tools

import androidx.fragment.app.Fragment
import androidx.test.rule.ActivityTestRule
import us.kostenko.architecturecomponentstmdb.android.SingleFragmentActivity

class FragmentTestRule : ActivityTestRule<SingleFragmentActivity>(SingleFragmentActivity::class.java, false, false) {

    fun launchFragment(fragment: Fragment) {
        launchActivity(null)
        activity.setFragment(fragment)
    }
}