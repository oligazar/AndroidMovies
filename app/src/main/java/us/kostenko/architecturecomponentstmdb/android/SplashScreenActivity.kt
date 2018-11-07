package us.kostenko.architecturecomponentstmdb.android

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

/**
 * A simple {@link Activity} subclass.
 */
class SplashScreenActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        intent = Intent(this, MainActivity::class.java)
        startActivityAndFinishCurrent(intent)
    }

    private fun startActivityAndFinishCurrent(intent: Intent) {
        startActivity(intent)
        finish()
    }
}
