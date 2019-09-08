package de.ahahn94.manhattan.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import de.ahahn94.manhattan.R
import de.ahahn94.manhattan.utils.ContextProvider
import de.ahahn94.manhattan.utils.settings.Credentials

/**
 * Class to handle the main activity.
 */
class MainActivity : ToolbarActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        // Save application context into ContextProvider.
        ContextProvider.setApplicationContext(applicationContext)

        // Load activity layout.
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Show login activity if no credentials have previously been saved.
        val showLogin = Credentials.getInstance().isEmpty()
        if (showLogin) switchToLoginActivity()
        else {
            // Sync database with ComicLib server at app start.
            startActivity(Intent(this, SyncActivity::class.java))
        }
    }

    /**
     * OnClick-function for the login button.
     */
    fun loginClicked(view: View) {
        switchToLoginActivity()
    }

    /**
     * OnClick-function for the volumeEntity overview button.
     */
    fun volumeOverviewClicked(view: View) {
        startActivity(Intent(this, VolumesOverviewActivity::class.java))
    }

    /**
     * OnClick-function for the publisherEntity overview button.
     */
    fun publisherOverviewClicked(view: View) {
        startActivity(Intent(this, PublishersOverviewActivity::class.java))
    }

    /**
     * Switch to the login activity.
     */
    private fun switchToLoginActivity() {
        startActivity(Intent(this, LoginActivity::class.java))
    }
}
