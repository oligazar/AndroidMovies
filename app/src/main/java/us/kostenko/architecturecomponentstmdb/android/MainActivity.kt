package us.kostenko.architecturecomponentstmdb.android

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import us.kostenko.architecturecomponentstmdb.R
import us.kostenko.architecturecomponentstmdb.common.utils.inTransaction
import us.kostenko.architecturecomponentstmdb.master.view.MoviesFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (savedInstanceState == null) {
            supportFragmentManager.inTransaction {
//                replace(R.id.container, MovieDetailFragment.create(MOVIE_ID))
                replace(R.id.container, MoviesFragment())
            }
        }

//        throw RuntimeException("This is a crash")
    }
}
