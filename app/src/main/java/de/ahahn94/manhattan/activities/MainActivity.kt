package de.ahahn94.manhattan.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import de.ahahn94.manhattan.R
import de.ahahn94.manhattan.utils.ContextProvider
import de.ahahn94.manhattan.utils.settings.Credentials

/**
 * Class to handle the app startup:
 * - show login if not logged in
 * - sync database with server
 * - start VolumesActivity
 */
class MainActivity : ToolbarActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        // Save application context into ContextProvider.
        ContextProvider.setApplicationContext(applicationContext)

        // Load activity layout.
        super.onCreate(savedInstanceState)

        // Show login activity if no credentials have previously been saved.
        val showLogin = Credentials.getInstance().isEmpty()
        if (showLogin) switchToLoginActivity()
        else {
            // Sync database with ComicLib server at app start.
            startActivityForResult(Intent(this, SyncActivity::class.java), 1)
            startActivity(Intent(this, VolumesActivity::class.java))
        }
    }

    /**
     * Switch to the login activity.
     */
    private fun switchToLoginActivity() {
        startActivity(Intent(this, LoginActivity::class.java))
    }
}
