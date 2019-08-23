package de.ahahn94.manhattan.cache

import de.ahahn94.manhattan.utils.ContextProvider
import java.io.File

/**
 * Class that handles the cache for the comic files.
 */
class ComicsCache {

    companion object {

        // Path to the comics cache.
        private const val COMICS_CACHE_PATH = "cache/comics"

        // Singleton instance of the cache directory.
        private lateinit var instance: File

        /**
         * Get the instance of the cache directory.
         * Initialize it if necessary.
         */
        fun getInstance(): File {
            if (!this::instance.isInitialized) {
                instance = File(ContextProvider.getApplicationContext().filesDir, COMICS_CACHE_PATH)
                instance.mkdirs()
            }
            return instance
        }

        /**
         * Get a list of the files contained in the cache.
         */
        fun getFilesList(): Array<File>? {
            return instance.listFiles()
        }

        /**
         * Get a list of the file names in the cache.
         */
        fun getFilesNames(): Array<String>? {
            return instance.list()
        }

    }

}