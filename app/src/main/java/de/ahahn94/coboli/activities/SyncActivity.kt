package de.ahahn94.coboli.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import de.ahahn94.coboli.R
import de.ahahn94.coboli.synchronisation.SyncManager
import de.ahahn94.coboli.utils.ContextProvider
import de.ahahn94.coboli.utils.Logging
import de.ahahn94.coboli.utils.network.OnlineStatusManager

/**
 * Class to handle the sync activity.
 * Shows a 'syncing with server' screen while syncing with the ComicLib server.
 */
class SyncActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        // Save application context into ContextProvider.
        ContextProvider.setApplicationContext(applicationContext)

        // Load activity layout.
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sync)

        // If connected to the server, sync collections in the background.
        syncWithServer()

    }

    /**
     * If connected to the server, sync the collections in the background.
     * Show a loading screen while updating the local collection to prevent
     * changes to the database by the user.
     * Show the LoginActivity if the connection fails due to authorization error.
     */
    private fun syncWithServer() {
        OnlineStatusManager.executeIfConnected {
            when (it) {
                OnlineStatusManager.SimpleStatus.OK -> {
                    Logging.logDebug("Connected to the ComicLib server.")
                    SyncManager.runSyncInBackground(this) {
                        // Go back to previous activity after sync has finished.
                        finish()
                    }
                }
                OnlineStatusManager.SimpleStatus.NO_CONNECTION -> {
                    Logging.logDebug("No connection to the server!")
                    Toast.makeText(
                        ContextProvider.getApplicationContext(),
                        R.string.sync_no_connection, Toast.LENGTH_LONG
                    ).show()
                    // Go back to previous activity.
                    finish()
                }
                OnlineStatusManager.SimpleStatus.UNAUTHORIZED -> {
                    Toast.makeText(
                        ContextProvider.getApplicationContext(),
                        R.string.login_failed, Toast.LENGTH_LONG
                    ).show()
                    startActivityForResult(Intent(this, LoginActivity::class.java), 1)
                    finish()
                }
            }
        }
    }

    /**
     * Ignore back-button.
     */
    override fun onBackPressed() {
        // Do nothing.
    }

}
