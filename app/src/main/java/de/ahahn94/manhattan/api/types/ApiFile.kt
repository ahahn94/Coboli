package de.ahahn94.manhattan.api.types

import android.util.Base64
import java.io.File

/**
 * Data class for files received from  the ComicLib API.
 */
data class ApiFile(val filename: String, val content: ByteArray?) {

    /**
     * Check if two files are identical.
     * Will return true if they are the same object or if they have the same filename and content.
     * Else false.
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ApiFile

        if (filename != other.filename) return false
        if (content != null) {
            if (other.content == null) return false
            if (!content.contentEquals(other.content)) return false
        } else if (other.content != null) return false

        return true
    }

    /**
     * Generate a hash code of the object.
     */
    override fun hashCode(): Int {
        var result = filename.hashCode()
        result = 31 * result + (content?.contentHashCode() ?: 0)
        return result
    }

    /**
     * Custom toString method.
     * Contains the filename and the Base64 encoded content.
     */
    override fun toString(): String {
        val bytesAsBase64 = Base64.encodeToString(content, Base64.NO_WRAP)
        return "ApiFile(filename=$filename, content=$bytesAsBase64)"
    }

    /**
     * Save the content of the ApiFile to the specified directory.
     */
    fun saveFile(directory: File) {
        val file = File(directory, filename)
        if (content != null) {
            file.writeBytes(content)
        }
    }

}