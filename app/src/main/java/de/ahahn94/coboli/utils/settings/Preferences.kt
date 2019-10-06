package de.ahahn94.coboli.utils.settings

import android.content.Context
import android.content.SharedPreferences
import de.ahahn94.coboli.utils.ContextProvider

/**
 * Class that handles access to the apps preferences.
 */
class Preferences {

    companion object {

        // Singleton instance of SharedPreferences.
        private lateinit var instance: SharedPreferences

        // Application context.
        private lateinit var applicationContext: Context

        // Constants for the instance.
        private const val PREFERENCES_NAME = "de.ahahn94.coboli"

        // Keyname constants.
        const val SERVER_ADDRESS_KEY = "serveraddress"

        /**
         * Initialize the applicationContext and instance if necessary.
         */
        private fun init() {
            if (!this::applicationContext.isInitialized) {
                applicationContext = ContextProvider.getApplicationContext()
            }
            if (!this::instance.isInitialized) {
                instance = applicationContext.getSharedPreferences(
                    PREFERENCES_NAME, Context.MODE_PRIVATE
                )
            }
        }

        /**
         * Get the singleton instance of the preferences.
         * Initializes the SharedPreferences of the app if necessary
         * before returning it.
         */
        fun getInstance(): SharedPreferences {
            if (!this::instance.isInitialized) {
                // Not yet initialized. Initialize and return instance.
                init()
            }
            return instance
        }

        /*
        Wrappers to make changes to a single key-value-pair of the settings a one-liner.
         */

        /**
         * Add a key-value-pair to the settings.
         * Creates a new editor on the preferences,
         * adds the new pair and commits the changes.
         */
        fun putString(key: String, value: String) {
            val editor = getInstance().edit()
            editor.putString(key, value)
            editor.apply()
        }

        /**
         * Add a key-value-pair to the settings.
         * Creates a new editor on the preferences,
         * adds the new pair and commits the changes.
         */
        fun putBoolean(key: String, value: Boolean) {
            val editor = getInstance().edit()
            editor.putBoolean(key, value)
            editor.apply()
        }

        /**
         * Add a key-value-pair to the settings.
         * Creates a new editor on the preferences,
         * adds the new pair and commits the changes.
         */
        fun putInt(key: String, value: Int) {
            val editor = getInstance().edit()
            editor.putInt(key, value)
            editor.apply()
        }

        /**
         * Add a key-value-pair to the settings.
         * Creates a new editor on the preferences,
         * adds the new pair and commits the changes.
         */
        fun putFloat(key: String, value: Float) {
            val editor = getInstance().edit()
            editor.putFloat(key, value)
            editor.apply()
        }

        /**
         * Add a key-value-pair to the settings.
         * Creates a new editor on the preferences,
         * adds the new pair and commits the changes.
         */
        fun putLong(key: String, value: Long) {
            val editor = getInstance().edit()
            editor.putLong(key, value)
            editor.apply()
        }

        /**
         * Add a key-value-pair to the settings.
         * Creates a new editor on the preferences,
         * adds the new pair and commits the changes.
         */
        fun putStringSet(key: String, value: MutableSet<String>) {
            val editor = getInstance().edit()
            editor.putStringSet(key, value)
            editor.apply()
        }

    }
}