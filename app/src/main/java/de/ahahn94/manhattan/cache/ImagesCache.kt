package de.ahahn94.manhattan.cache

import de.ahahn94.manhattan.utils.ContextProvider
import java.io.File

/**
 * Class that handles the cache for the image files.
 */
class ImagesCache {

    companion object {

        // Path to the images cache.
        private const val IMAGE_CACHE_PATH = "cache/images"

        // Singleton instance of the cache directory.
        private lateinit var instance: File

        /**
         * Get the instance of the cache directory.
         * Initialize it if necessary.
         */
        fun getInstance(): File {
            if (!this::instance.isInitialized) {
                instance = File(ContextProvider.getApplicationContext().filesDir, IMAGE_CACHE_PATH)
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