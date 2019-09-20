package de.ahahn94.manhattan.comicextractors

import android.widget.Toast
import de.ahahn94.manhattan.R
import de.ahahn94.manhattan.utils.ContextProvider
import de.ahahn94.manhattan.utils.Localization
import de.ahahn94.manhattan.utils.Logging

/**
 * Custom exception class for exceptions while unpacking comic files.
 */
class ExtractorException(private val type: Type, val exception: Exception?) : Exception() {

    /**
     * Run predefined functions based on exception type.
     * Needs to run on the main thread because of Toasts.
     */
    fun handleException() {
        when (type) {
            Type.GENERAL -> if (exception != null) throw exception
            Type.RAR_NOT_SUPPORTED -> toastError(Localization.getLocalizedString(R.string.cbr_not_supported))
            Type.FILE_ENCRYPTED -> toastError(Localization.getLocalizedString(R.string.file_encrypted))
        }
    }

    /**
     * Show the error message as a Toast.
     * Needs to run on the main thread.
     */
    private fun toastError(message: String) {
        Toast.makeText(
            ContextProvider.getApplicationContext(),
            message,
            Toast.LENGTH_LONG
        ).show()
        Logging.logError(message)
    }

    /**
     * Exception types.
     * RAR_NOT_SUPPORTED: exception due to version incompatibility. Show Toast.
     * FILE_ENCRYPTED: exception due to encryption of the comic file. Show Toast.
     * GENERAL: any other exception. Throw exception.
     */
    enum class Type {
        GENERAL,
        RAR_NOT_SUPPORTED,
        FILE_ENCRYPTED
    }

}