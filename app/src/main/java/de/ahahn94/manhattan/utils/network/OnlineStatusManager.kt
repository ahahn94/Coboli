package de.ahahn94.manhattan.utils.network

import android.net.ConnectivityManager
import android.os.AsyncTask
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import de.ahahn94.manhattan.api.clients.TrustedCertificatesClientFactory
import de.ahahn94.manhattan.utils.ContextProvider
import de.ahahn94.manhattan.utils.settings.Credentials
import de.ahahn94.manhattan.utils.settings.Preferences
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

/**
 * Handles the online status of the app.
 * Checks if connected to a network and the ComicLib server.
 */
class OnlineStatusManager {

    companion object {

        // LiveData of the current connection status. Tested against the /online resource and
        // updated every 10 seconds.
        val connectionStatus = object : MutableLiveData<Boolean>() {}

        /**
         * Check if the device is connected to a network.
         * Returns true if connected, else false.
         * Does not check for internet connection, but only for network connection.
         */
        private fun connectedToNetwork(): Boolean {
            val connectivityManager = ContextCompat.getSystemService(
                ContextProvider.getApplicationContext(),
                ConnectivityManager::class.java
            )
            val networkInfo = connectivityManager?.activeNetworkInfo
            return networkInfo != null && networkInfo.isConnected
        }

        /**
         * Check if the device can successfully establish a connection to the ComicLib server.
         * Return true if successfully connected, else false.
         * Will update the API key if successfully connected and API key changed.
         */
        private fun connectedToTokensResource(): Boolean {
            // Check if the status type is OK. Else error (connection, https or authentication).
            val status = ConnectionTester.test()
            return if (status.statusType == ConnectionStatusType.OK) {
                // Check if API keys match. Else update (may have been regenerated via web interface).
                val apiKeyFromServer = status.response?.body()?.responseContent?.apiKey
                if (apiKeyFromServer != null && Credentials.getInstance().apiKey != apiKeyFromServer) {
                    Credentials.getInstance().apiKey = apiKeyFromServer
                    Credentials.saveInstance()
                }
                true
            } else false
        }

        /**
         * Check if the device can successfully establish
         * a connection to the /online resource of the ComicLib server.
         * Return true if successfully connected, else false.
         * Uses a shorter timeout on the connection than default.
         */
        private fun connectedToOnlineResource(): Boolean {
            val timeout = 1000L  // The response body is really small, so 1000ms should be enough.
            val onlineApiPath = "/online"
            val serverAddress =
                Preferences.getInstance().getString(Preferences.SERVER_ADDRESS_KEY, "") ?: ""
            val client =
                TrustedCertificatesClientFactory.createPreconfiguredBuilder()
                    .callTimeout(timeout, TimeUnit.MILLISECONDS)
                    .build()
            val request = Request.Builder().url("$serverAddress$onlineApiPath").build()
            val response: Response
            return try {
                response = client.newCall(request).execute()
                response.isSuccessful
            } catch (e: IOException) {
                // Timeout raises  an exception. Catch and return false.
                false
            }
        }

        /**
         * Check if the device can successfully connect to the ComicLib server.
         * Will update the saved API token if it has changed.
         * Return true if successfully connected, else false.
         * Has to run in a non-UI thread.
         */
        fun connected(): Boolean {
            // Check if connected to a network. Else return false.
            return if (connectedToNetwork()) {
                // Check if connected to the server.
                connectedToTokensResource()
            } else false
        }

        /**
         * Quickly check if the device can successfully connect to the ComicLib server.
         * Will use the /online resource to avoid frequent access to the /tokens resource.
         * Return true if successfully connected, else false.
         * Has to run in a non-UI thread.
         */
        private fun quickConnectionTest(): Boolean {
            // Check if connected to a network. Else return false.
            return if (connectedToNetwork()) {
                // Check if connected to the server.
                connectedToOnlineResource()
            } else false
        }

        /**
         * Run the passed function if successfully connected to the server.
         */
        fun executeIfConnected(function: (isConnected: Boolean) -> Unit) {
            OnlineStatusChecker(function).execute()
        }

        /**
         * Start the online status monitoring.
         * Will update connectionStatus with the current connection status
         * every 10 seconds.
         */
        fun startOnlineStatusMonitor() {
            val scheduler = Executors.newSingleThreadScheduledExecutor()
            scheduler.scheduleAtFixedRate({
                connectionStatus.postValue(quickConnectionTest())
            }, 0, 10, TimeUnit.SECONDS)
        }

    }

    /**
     * AsyncTask that runs the connected()-function in the background and executes the passed
     * function with the result.
     */
    private class OnlineStatusChecker(val function: (isConnected: Boolean) -> Unit) :
        AsyncTask<(Boolean) -> Unit, Int, Boolean>() {

        override fun doInBackground(vararg params: ((isConnected: Boolean) -> Unit)?): Boolean {
            return connected()
        }

        override fun onPostExecute(result: Boolean?) {
            function(result!!)
        }

    }

}