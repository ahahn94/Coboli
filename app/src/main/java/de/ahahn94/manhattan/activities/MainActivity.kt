package de.ahahn94.manhattan.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import de.ahahn94.manhattan.R
import de.ahahn94.manhattan.synchronisation.SyncManager
import de.ahahn94.manhattan.utils.ContextProvider
import de.ahahn94.manhattan.utils.Logging
import de.ahahn94.manhattan.utils.network.OnlineStatusManager
import de.ahahn94.manhattan.utils.settings.Credentials

/**
 * Class to handle the main activity.
 */
class MainActivity : AppCompatActivity() {

    lateinit var buttons: LinearLayout
    lateinit var syncCardView: CardView

    override fun onCreate(savedInstanceState: Bundle?) {

        // Save application context into ContextProvider.
        ContextProvider.setApplicationContext(applicationContext)

        // Load activity layout.
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        buttons = this.findViewById(R.id.buttons)
        syncCardView = this.findViewById(R.id.syncCardView)

        // Show login activity if no credentials have previously been saved.
        val showLogin = Credentials.getInstance().isEmpty()
        if (showLogin) switchToLoginActivity()
    }

    override fun onResume() {
        super.onResume()

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

                SyncManager.runSyncInBackground(
                    this::showSyncAnimation
                ) {
                    Logging.logDebug("Sync done.")
                    hideSyncAnimation()
                }

            } else {
                hideSyncAnimation()
                Logging.logDebug("No connection to the server!")
            }
        }
    }

    /**
     * Show sync animation and hide buttons.
     */
    fun showSyncAnimation() {
        syncCardView.visibility = View.VISIBLE
        buttons.visibility = View.GONE
    }

    /**
     * Hide sync animation and show buttons.
     */
    fun hideSyncAnimation() {
        runOnUiThread() {
            syncCardView.visibility = View.GONE
            buttons.visibility = View.VISIBLE
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
