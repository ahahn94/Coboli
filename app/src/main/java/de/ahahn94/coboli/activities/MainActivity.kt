package de.ahahn94.coboli.activities

import android.content.Intent
import android.os.Bundle
import de.ahahn94.coboli.utils.ContextProvider
import de.ahahn94.coboli.utils.settings.Credentials

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

        // Sync database at startup if credentials are present.
        if (startup && Credentials.isEmpty.value == false) {
            startup = false
            // Sync database with ComicLib server at app start.
            startActivityForResult(Intent(this, SyncActivity::class.java), 1)
        }
    }
}
