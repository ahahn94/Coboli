package de.ahahn94.coboli.utils

import android.content.Context

/**
 * Class to provide applicationContext as a singleton.
 */
class ContextProvider {

    companion object {

        // Context in a static field can be a memory leak. This will only store the application context, which is a
        // singleton anyway, so no leak.
        private lateinit var applicationContext: Context

        /**
         * Take the applicationContext from a context and store it.
         */
        fun setApplicationContext(context: Context) {
            if (!this::applicationContext.isInitialized) {
                applicationContext = context.applicationContext
            }
        }

        /**
         * Get the singleton applicationContext.
         */
        fun getApplicationContext(): Context {
            return applicationContext
        }
    }

}