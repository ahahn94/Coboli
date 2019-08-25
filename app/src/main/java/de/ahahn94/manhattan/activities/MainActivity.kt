package de.ahahn94.manhattan.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import de.ahahn94.manhattan.R
import de.ahahn94.manhattan.utils.ContextProvider
import de.ahahn94.manhattan.utils.settings.Credentials

/**
 * Class to handle the main activity.
 */
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        // Save application context into ContextProvider.
        ContextProvider.setApplicationContext(applicationContext)

        // Load activity layout.
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Show login activity if no credentials have previously been saved.
        val showLogin = Credentials.getInstance().isEmpty()
        if (showLogin) switchToLoginActivity()

    }

    fun loginClicked(view: View) {
        switchToLoginActivity()
    }

    private fun switchToLoginActivity() {
        startActivity(Intent(this, LoginActivity::class.java))
    }
}
