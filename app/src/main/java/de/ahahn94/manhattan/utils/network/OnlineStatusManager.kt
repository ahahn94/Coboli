package de.ahahn94.manhattan.utils.network

import android.net.ConnectivityManager
import android.os.AsyncTask
import androidx.core.content.ContextCompat
import de.ahahn94.manhattan.utils.ContextProvider
import de.ahahn94.manhattan.utils.settings.Credentials

/**
 * Handles the online status of the app.
 * Checks if connected to a network and the ComicLib server.
 */
class OnlineStatusManager {

    companion object {

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
        private fun connectedToTheServer(): Boolean {
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
         * Check if the device can successfully connected to the ComicLib server.
         * Return true if successfully connected, else false.
         */
        private fun connected(): Boolean {
            // Check if connected to a network. Else return false.
            return if (connectedToNetwork()) {
                // Check if connected to the server.
                connectedToTheServer()
            } else false
        }

        /**
         * Run the passed function if successfully connected to the server.
         */
        fun executeIfConnected(function: (isConnected: Boolean) -> Unit) {
            OnlineStatusChecker(function).execute()
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