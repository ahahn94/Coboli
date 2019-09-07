package de.ahahn94.manhattan.cache

import android.os.AsyncTask
import de.ahahn94.manhattan.api.repos.ComicLibImages
import de.ahahn94.manhattan.utils.ContextProvider
import de.ahahn94.manhattan.utils.Logging
import de.ahahn94.manhattan.utils.replaceNull
import de.ahahn94.manhattan.utils.settings.Preferences
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

        private lateinit var comicLibImages: ComicLibImages

        /**
         * Initialize the instance and comicLibImages.
         */
        fun init() {
            if (!this::instance.isInitialized) {
                instance = File(ContextProvider.getApplicationContext().filesDir, IMAGE_CACHE_PATH)
                instance.mkdirs()
            }
            if (!this::comicLibImages.isInitialized) {
                val serverAddress =
                    Preferences.getInstance().getString(
                        Preferences.SERVER_ADDRESS_KEY,
                        ""
                    ) replaceNull ""
                comicLibImages = ComicLibImages(serverAddress)
            }
        }

        /**
         * Get the instance of the cache directory.
         * Initialize it if necessary.
         */
        fun getInstance(): File {
            if (!this::instance.isInitialized) {
                init()
            }
            return instance
        }

        /**
         * Get a list of the files contained in the cache.
         */
        fun getFilesList(): Array<File>? {
            return getInstance().listFiles()
        }

        /**
         * Get a list of the file names in the cache.
         */
        fun getFilesNames(): Array<String>? {
            return getInstance().list()
        }

        /**
         * Get the absolute path of a cached image file.
         * Takes the API URL of an image file.
         * Returns the absolute path if the file exists or null if not.
         */
        fun getImageFilePath(imageURL : String): String? {
            val fileName = imageURL.split("/").last()
            val file = File(getInstance(), fileName)
            return if (file.exists()) file.absolutePath else null
        }

        /**
         * Download and cache an image file.
         * Runs in a new thread.
         */
        fun cacheImageFile(imageURL: String) {
            Logging.logDebug("Caching image file $imageURL")
            ImagesCacher(imageURL).execute()
        }

        /**
         * Delete a cached image file.
         * Runs in a new thread.
         */
        fun deleteImage(imageURL: String) {
            Logging.logDebug("Deleting image file ${imageURL.split("/").last()}")
            ImagesDeleter(imageURL)
        }

    }

    /**
     * AsyncTask that runs downloading and caching of image files in the background.
     */
    class ImagesCacher(private val imageURL: String) :
        AsyncTask<String, Int, Unit>() {

        override fun doInBackground(vararg params: String?) {
            val response = comicLibImages.getImage(imageURL)
            response?.saveFile(getInstance())
            Logging.logDebug("Image ${imageURL.split("/").last()} saved.")
        }

    }

    /**
     * AsyncTask that runs deletion of cached image files in the background.
     */
    class ImagesDeleter(private val imageURL: String) :
        AsyncTask<String, Int, Unit>() {

        override fun doInBackground(vararg params: String?) {
            val fileName = imageURL.split("/").last()
            val imageFile = File(getInstance(), fileName)
            if (imageFile.exists()) {
                imageFile.delete()
                Logging.logInfo("Image $fileName successfully deleted.")
            } else {
                Logging.logError("Could not delete image file $fileName! File does not exist.")
            }
        }

    }

}