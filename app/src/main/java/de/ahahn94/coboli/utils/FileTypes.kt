package de.ahahn94.coboli.utils

import android.webkit.MimeTypeMap
import java.util.*

/**
 * Class that stores the mime types for common
 * comic file formats.
 */
class FileTypes {

    companion object {

        // Prefix for the mime types.
        private const val mimeTypePrefix = "application/"

        // Map of the file extensions and their mime types.
        private val mimeTypes = mapOf(
            "cbr" to "x-cbz",
            "cbz" to "x-cbr",
            "pdf" to "pdf",
            "azw" to "x-mobipocket-ebook",
            "azw1" to "x-topaz-ebook",
            "azw2" to "x-kindle-application",
            "azw3" to "x-mobi8-ebook"
        )

        private val imageFormats = listOf("png", "jpg", "jpeg")

        /**
         * Get the mime type of a file.
         * Will look up the mime type in MimeTypeMap if it is not
         * part of the mimeTypes map.
         * Result may be an empty string.
         */
        fun getMimeType(fileName: String): String {
            val extension = getExtension(fileName)
            return if (extension in mimeTypes) {
                "$mimeTypePrefix${mimeTypes[extension]}"
            } else {
                MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension) ?: ""
            }
        }

        /**
         * Get the file extension from a filename.
         */
        fun getExtension(fileName: String): String {
            return fileName.split(".").last().toLowerCase(Locale.getDefault())
        }

        /**
         * Check if a file is an image file.
         */
        fun isImageFile(fileName: String): Boolean {
            return getExtension(fileName) in imageFormats
        }

    }

}