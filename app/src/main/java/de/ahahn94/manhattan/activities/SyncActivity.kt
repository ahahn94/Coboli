package de.ahahn94.manhattan.activities

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import de.ahahn94.manhattan.R
import de.ahahn94.manhattan.synchronisation.SyncManager
import de.ahahn94.manhattan.utils.ContextProvider
import de.ahahn94.manhattan.utils.Logging
import de.ahahn94.manhattan.utils.network.OnlineStatusManager

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
     */
    private fun syncWithServer() {
        OnlineStatusManager.executeIfConnected {
            if (it) {
                Logging.logDebug("Connected to the ComicLib server.")

                SyncManager.runSyncInBackground(this) {

                    // Go back to previous activity after sync has finished.
                    finish()
                }

            } else {
                Logging.logDebug("No connection to the server!")
                Toast.makeText(
                    ContextProvider.getApplicationContext(),
                    R.string.sync_no_connection, Toast.LENGTH_LONG
                ).show()
                // Go back to previous activity.
                finish()
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
