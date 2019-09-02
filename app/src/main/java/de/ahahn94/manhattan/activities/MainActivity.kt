package de.ahahn94.manhattan.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import de.ahahn94.manhattan.R
import de.ahahn94.manhattan.synchronisation.SyncManager
import de.ahahn94.manhattan.utils.ContextProvider
import de.ahahn94.manhattan.utils.Localization
import de.ahahn94.manhattan.utils.Logging
import de.ahahn94.manhattan.utils.network.OnlineStatusManager
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

        // If connected to the server, sync collections in the background.
        OnlineStatusManager.executeIfConnected {
            if (it) {
                Logging.logDebug("Connected to the ComicLib server.");
                Toast.makeText(
                    this,
                    Localization.getLocalizedString(R.string.connected_starting_sync),
                    Toast.LENGTH_LONG
                ).show()
                SyncManager.runSyncInBackground()
            } else {
                Logging.logDebug("No connection to the server!")
                Toast.makeText(
                    this,
                    Localization.getLocalizedString(R.string.no_connection),
                    Toast.LENGTH_LONG
                ).show()
            }
        }

    }

    fun loginClicked(view: View) {
        switchToLoginActivity()
    }

    private fun switchToLoginActivity() {
        startActivity(Intent(this, LoginActivity::class.java))
    }
}
