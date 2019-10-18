package de.ahahn94.coboli.utils.settings

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.MutableLiveData
import de.ahahn94.coboli.utils.ContextProvider
import de.ahahn94.coboli.utils.security.Cryptography

/**
 * Class that handles the users credentials.
 */
data class Credentials(
    var username: String = "",
    var password: String = "",
    var apiKey: String = ""
) {

    companion object {

        // Singleton instance of Credentials.
        lateinit var instance: Credentials

        // Singleton instance of preferences.
        private lateinit var sharedPreferences: SharedPreferences

        // Application context (used to access SharedPreferences of the app).
        private lateinit var applicationContext: Context

        // Constants for the credentials.
        private const val CREDENTIALS_PREFERENCES_NAME = "de.ahahn94.coboli.credentials"
        private const val USERNAME_KEY = "username"
        private const val PASSWORD_KEY = "password"
        private const val APIKEY_KEY = "apikey"

        // Status of the credentials as live data.
        val isEmpty = MutableLiveData<Boolean>()

        init {
            // Init isEmpty with false so ToolbarActivity does not start LoginActivity at app startup.
            isEmpty.postValue(false)
        }

        /**
         * Initialize the applicationContext and sharedPreferences if necessary.
         */
        private fun init() {
            if (!this::applicationContext.isInitialized) {
                applicationContext = ContextProvider.getApplicationContext()
            }
            if (!this::sharedPreferences.isInitialized) {
                sharedPreferences = applicationContext.getSharedPreferences(
                    CREDENTIALS_PREFERENCES_NAME, Context.MODE_PRIVATE
                )
            }
        }

        /**
         * Get the singleton instance of the preferences.
         * Initializes the SharedPreferences of the app if necessary
         * before returning it.
         */
        private fun getSharedPreferences(): SharedPreferences {
            if (!this::sharedPreferences.isInitialized) {
                // Not yet initialized. Initialize and return sharedPreferences.
                init()
            }
            return sharedPreferences
        }

        /**
         * Load the singleton instance of the credentials.
         * Initialize the credentials from the SharedPreferences if not yet initialized.
         * Credentials from sharedPreferences are stored encrypted and have to be decrypted during initialization.
         * Instance will be consist of empty strings if no credentials where stored in sharedPreferences.
         */
        fun loadInstance() {
            if (!this::instance.isInitialized) {
                // Not yet initialized. Initialize it from SharedPreferences.
                val sharedPreferences =
                    getSharedPreferences()
                // Try to read the credentials from the preferences file. )
                val rawUsername: String =
                    sharedPreferences.getString(USERNAME_KEY, "") ?: ""
                val rawPassword: String =
                    sharedPreferences.getString(PASSWORD_KEY, "") ?: ""
                val rawApiKey: String = sharedPreferences.getString(APIKEY_KEY, "") ?: ""

                if (isEmpty(
                        Credentials(
                            rawUsername,
                            rawPassword,
                            rawApiKey
                        )
                    )
                ) {
                    // No stored credentials.
                    instance = Credentials()
                    isEmpty.postValue(true)
                    return
                }

                // Try to decrypt loaded credentials.
                val username = if (rawUsername != "") {
                    Cryptography.decryptToString(rawUsername)
                } else ""
                val password = if (rawPassword != "") {
                    Cryptography.decryptToString(rawPassword)
                } else ""
                val apiKey = if (rawApiKey != "") {
                    Cryptography.decryptToString(rawApiKey)
                } else ""
                instance = Credentials(username, password, apiKey)
                isEmpty.postValue(isEmpty(instance))
            }
        }

        /**
         * Save credentials to SharedPreferences.
         * Username, Password and apiKey will be encrypted before storing.
         */
        fun saveInstance() {
            val sharedPreferences = getSharedPreferences()
            val editor = sharedPreferences.edit()
            val encryptedUsername =
                Cryptography.encryptToBase64(instance.username)
            val encryptedPassword =
                Cryptography.encryptToBase64(instance.password)
            val encryptedApiKey =
                Cryptography.encryptToBase64(instance.apiKey)
            editor.putString(USERNAME_KEY, encryptedUsername)
                .putString(PASSWORD_KEY, encryptedPassword)
                .putString(APIKEY_KEY, encryptedApiKey)
            editor.apply()
            isEmpty.postValue(false)
        }

        /**
         * Reset the credentials.
         * E.g. after password change.
         */
        fun reset() {
            with(instance) {
                username = ""
                password = ""
                apiKey = ""
            }
            saveInstance()
            isEmpty.postValue(true)
        }

        /**
         * Check if credentials are empty.
         */
        private fun isEmpty(credentials: Credentials): Boolean {
            with(credentials) {
                return username == "" && password == "" && apiKey == ""
            }
        }

    }

}