package de.ahahn94.coboli.utils

/**
 * Class that handles localization.
 */
class Localization {

    companion object {

        /**
         * Get the localized version of a string.
         * Takes the resource-id of the string and (optionally) the parameters for the
         * placeholders inside the string (if any).
         */
        fun getLocalizedString(id: Int, vararg args: Any?): String {
            return ContextProvider.getApplicationContext().getString(id, *args)
        }

    }

}