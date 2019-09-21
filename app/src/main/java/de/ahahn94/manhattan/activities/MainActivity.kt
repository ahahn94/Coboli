package de.ahahn94.manhattan.activities

import android.content.Intent
import android.os.Bundle
import de.ahahn94.manhattan.utils.ContextProvider
import de.ahahn94.manhattan.utils.settings.Credentials

/**
 * Class to handle the app startup:
 * - show login if not logged in
 * - sync database with server
 * - start VolumesFragment
 */
class MainActivity : ToolbarActivity() {

    companion object {

        private var startup = true

    }

    override fun onCreate(savedInstanceState: Bundle?) {

        // Save application context into ContextProvider.
        ContextProvider.setApplicationContext(applicationContext)

        // Load activity layout.
        super.onCreate(savedInstanceState)

        // Show login activity if no credentials have previously been saved.
        val showLogin = Credentials.getInstance().isEmpty()
        if (showLogin){
            // Require login and sync database with server after successful login.
            startActivityForResult(Intent(this, LoginActivity::class.java), 1)
        }
        else {
            if (startup){
                startup = false
                // Sync database with ComicLib server at app start.
                startActivityForResult(Intent(this, SyncActivity::class.java), 1)
            }
        }
    }

}
