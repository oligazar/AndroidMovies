package us.kostenko.architecturecomponentstmdb.android

import android.os.Bundle
import android.support.v4.app.FragmentManager
import android.support.v7.app.AppCompatActivity
import us.kostenko.architecturecomponentstmdb.R
import us.kostenko.architecturecomponentstmdb.common.utils.inTransaction
import us.kostenko.architecturecomponentstmdb.master.view.MoviesFragment

class MainActivity : AppCompatActivity(), FragmentManager.OnBackStackChangedListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportFragmentManager.addOnBackStackChangedListener(this)

        if (savedInstanceState == null) {
            supportFragmentManager.inTransaction {
//                replace(R.id.container, MovieDetailFragment.create(MOVIE_ID))
                replace(R.id.container, MoviesFragment())
                addToBackStack(null)
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        supportFragmentManager.popBackStack()
        return super.onSupportNavigateUp()
    }

    override fun onBackStackChanged() {
        shouldDisplayHomeUp()
    }

    private fun shouldDisplayHomeUp() { //  Enable Up button only if there are entries in the back stack
        val canBack = supportFragmentManager.backStackEntryCount > 1
        supportActionBar?.setDisplayHomeAsUpEnabled(canBack)
    }
}
