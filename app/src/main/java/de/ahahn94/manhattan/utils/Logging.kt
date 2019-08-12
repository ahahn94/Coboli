package de.ahahn94.manhattan.utils

import android.util.Log

/**
 * Class that handles logging of messages to the debugging log.
 */
class Logging {

    companion object {

        // Constants.
        private const val TAG = "Manhattan"

        /**
         * Log an info message.
         */
        fun logInfo(message: String) {
            Log.d(TAG, message)
        }

        /**
         * Log an error message.
         */
        fun logError(message: String) {
            Log.e(TAG, message)
        }

        /**
         * Log the stacktrace of an exception.
         */
        fun logStackTrace(e: Exception) {
            Log.e(TAG, "Exception", e)
        }
    }
}