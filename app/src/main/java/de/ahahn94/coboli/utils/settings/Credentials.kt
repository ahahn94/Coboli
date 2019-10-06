package de.ahahn94.coboli.utils.settings

import android.content.Context
import android.content.SharedPreferences
import de.ahahn94.coboli.utils.ContextProvider
import de.ahahn94.coboli.utils.replaceNull
import de.ahahn94.coboli.utils.security.Cryptography

/**
 * Class that handles the users credentials.
 */
data class Credentials(var username: String = "", var password: String = "", var apiKey: String = "") {

    companion object {

        // Singleton instance of Credentials.
        private val instance = Credentials()

        // Singleton instance of preferences.
        private lateinit var sharedPreferences: SharedPreferences

        // Application context (used to access SharedPreferences of the app).
        private lateinit var applicationContext: Context

        // Constants for the credentials.
        private const val CREDENTIALS_PREFERENCES_NAME = "de.ahahn94.coboli.credentials"
        private const val USERNAME_KEY = "username"
        private const val PASSWORD_KEY = "password"
        private const val APIKEY_KEY = "apikey"

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
         * Get the singleton instance of the credentials.
         * Initialize the credentials from the preferences if empty.
         * Credentials from sharedPreferences are stored encrypted and have to be decrypted during initialization.
         * Returns an Credentials with empty strings if no credentials where stored in sharedPreferences.
         */
        fun getInstance(): Credentials {
            if (instance.isEmpty()) {
                val sharedPreferences =
                    getSharedPreferences()
                // Try to read the credentials from the preferences file. )
                val rawUsername: String = sharedPreferences.getString(USERNAME_KEY, "") replaceNull ""
                val rawPassword: String = sharedPreferences.getString(PASSWORD_KEY, "") replaceNull ""
                val rawApiKey: String = sharedPreferences.getString(APIKEY_KEY, "") replaceNull ""

                if (Credentials(
                        rawUsername,
                        rawPassword,
                        rawApiKey
                    ).isEmpty()
                ) return instance // No stored credentials.

                // Try to decrypt loaded credentials.
                if (rawUsername != "") {
                    instance.username =
                        Cryptography.decryptToString(rawUsername)
                }
                if (rawPassword != "") {
                    instance.password =
                        Cryptography.decryptToString(rawPassword)
                }
                if (rawApiKey != "") {
                    instance.apiKey =
                        Cryptography.decryptToString(rawApiKey)
                }
                return instance
            } else {
                return instance
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
        }

    }

    /**
     * Check if credentials are empty.
     */
    fun isEmpty(): Boolean {
        return username == "" && password == "" && apiKey == ""
    }


}