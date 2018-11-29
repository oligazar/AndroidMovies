package us.kostenko.architecturecomponentstmdb.android

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import us.kostenko.architecturecomponentstmdb.R
import us.kostenko.architecturecomponentstmdb.common.utils.inTransaction

class SingleFragmentActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun setFragment(fragment: Fragment) {
        supportFragmentManager.inTransaction {
            replace(R.id.container, fragment)
            addToBackStack(null)
        }
    }
}