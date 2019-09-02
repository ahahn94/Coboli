package de.ahahn94.manhattan.utils

/**
 * Class that handles localization.
 */
class Localization {

    companion object {

        /**
         * Get the localized version of a string.
         */
        fun getLocalizedString(id: Int): String {
            return ContextProvider.getApplicationContext().getString(id)
        }

    }

}