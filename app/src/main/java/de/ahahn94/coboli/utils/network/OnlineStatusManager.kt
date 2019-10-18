package de.ahahn94.coboli.utils.network

import android.net.ConnectivityManager
import android.os.AsyncTask
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import de.ahahn94.coboli.api.clients.TrustedCertificatesClientFactory
import de.ahahn94.coboli.api.repos.ComicLibAPI
import de.ahahn94.coboli.utils.ContextProvider
import de.ahahn94.coboli.utils.Logging
import de.ahahn94.coboli.utils.settings.Credentials
import de.ahahn94.coboli.utils.settings.Preferences
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLHandshakeException

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
         * Check if the device can successfully establish an authenticated
         * connection to the ComicLib server.
         * Return OK if successfully connected, NO_CONNECTION if no connection and
         * UNAUTHORIZED if authorization failed..
         * Will update the API key if first try fails but username and password are still valid.
         */
        private fun authenticatedConnection(): SimpleStatus {

            // Check if connection to /online is possible.
            if (quickConnectionTest()) {
                // Check if API key is still valid.
                val response = ComicLibAPI(
                    Preferences.getInstance().getString(
                        Preferences.SERVER_ADDRESS_KEY,
                        ""
                    ) ?: ""
                ).getAuthenticated()

                if (response.isSuccessful) return SimpleStatus.OK
                else {
                    if (response.code() == 401) {
                        // Authentication failed. Try refreshing API key.
                        val token = ComicLibAPI(
                            Preferences.getInstance().getString(
                                Preferences.SERVER_ADDRESS_KEY,
                                ""
                            ) ?: ""
                        ).getToken()

                        if (token.isSuccessful) {
                            // Successfully loaded new API key. Save key.
                            val apiKeyFromServer = token.body()?.responseContent?.apiKey
                            if (apiKeyFromServer != null) {
                                with(Credentials.instance) {
                                    apiKey = apiKeyFromServer
                                }
                                Credentials.saveInstance()
                            }
                            return SimpleStatus.OK
                        } else {
                            return if (token.code() == 401) {
                                // Authentication failed again. Password was changed too.
                                // Reset credentials to force new login.
                                Logging.logDebug("Authorization failed.")
                                Credentials.reset()
                                SimpleStatus.UNAUTHORIZED
                            } else {
                                SimpleStatus.NO_CONNECTION
                            }
                        }

                    } else {
                        // Error connecting.
                        return SimpleStatus.NO_CONNECTION
                    }
                }
            } else return SimpleStatus.NO_CONNECTION

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
            if (serverAddress != "") {
                val client =
                    TrustedCertificatesClientFactory.createPreconfiguredBuilder()
                        .callTimeout(timeout, TimeUnit.MILLISECONDS)
                        .build()
                val request = Request.Builder().url("$serverAddress$onlineApiPath").build()
                val response: Response
                return try {
                    response = client.newCall(request).execute()
                    response.isSuccessful
                } catch (e: SSLHandshakeException) {
                    // Certificate changed. Reset credentials to force user to login again.
                    Credentials.reset()
                    false
                } catch (e: IOException) {
                    // Timeout raises  an exception. Catch and return false.
                    false
                }
            } else return false // No server address. Not yet logged in.
        }

        /**
         * Check if the device can successfully connect to the ComicLib server.
         * Will update the saved API token if it has changed.
         * Return OK if successfully connected, else NO_CONNECTION or UNAUTHORIZED.
         * Has to run in a non-UI thread.
         */
        fun connected(): SimpleStatus {
            // Check if connected to a network. Else return false.
            return if (connectedToNetwork()) {
                // Check if connected to the server.
                authenticatedConnection()
            } else SimpleStatus.NO_CONNECTION
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
        fun executeIfConnected(function: (isConnected: SimpleStatus) -> Unit) {
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
    private class OnlineStatusChecker(val function: (isConnected: SimpleStatus) -> Unit) :
        AsyncTask<(Boolean) -> Unit, Int, SimpleStatus>() {

        override fun doInBackground(vararg params: ((isConnected: Boolean) -> Unit)?): SimpleStatus {
            return connected()
        }

        override fun onPostExecute(result: SimpleStatus?) {
            function(result!!)
        }

    }

    /**
     * Possible results for the authenticatedConnection function.
     */
    enum class SimpleStatus {
        OK,
        UNAUTHORIZED,
        NO_CONNECTION
    }

}