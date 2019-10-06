package de.ahahn94.coboli.utils

import java.text.SimpleDateFormat
import java.util.*

/**
 * Class to handle UTC timestamps for the ReadStatus.
 */
class Timestamps {

    companion object {

        // Constants.
        const val DATE_FORMAT = "yyyy-MM-dd HH:mm:ss"

        /**
         * Turn a UTC timestamp into a Date.
         * Timestamp has to be formatted like DATE_FORMAT.
         */
        fun timeStampToDate(timeStamp: String): Date? {
            return SimpleDateFormat(DATE_FORMAT, Locale.getDefault()).parse(timeStamp)
        }

        /**
         * Get the UTC timestamp of the current time and date.
         * Uses the DATE_FORMAT pattern for formatting.
         */
        fun nowToUtcTimestamp(): String {
            val formatter = SimpleDateFormat(DATE_FORMAT, Locale.getDefault())
            formatter.timeZone = TimeZone.getTimeZone("UTC")
            return formatter.format(Date())
        }
    }

}