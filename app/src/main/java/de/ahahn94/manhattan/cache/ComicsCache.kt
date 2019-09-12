package de.ahahn94.manhattan.cache

import android.os.AsyncTask
import de.ahahn94.manhattan.api.repos.ComicLibComics
import de.ahahn94.manhattan.model.Database
import de.ahahn94.manhattan.model.entities.CachedComicEntity
import de.ahahn94.manhattan.utils.ContextProvider
import de.ahahn94.manhattan.utils.Logging
import de.ahahn94.manhattan.utils.replaceNull
import de.ahahn94.manhattan.utils.settings.Preferences
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

        private lateinit var comicLibComics: ComicLibComics

        /**
         * Initialize the instance and comicLibComics.
         */
        fun init() {
            if (!this::instance.isInitialized) {
                instance = File(ContextProvider.getApplicationContext().filesDir, COMICS_CACHE_PATH)
                instance.mkdirs()
            }
            if (!this::comicLibComics.isInitialized) {
                val serverAddress =
                    Preferences.getInstance().getString(
                        Preferences.SERVER_ADDRESS_KEY,
                        ""
                    ) replaceNull ""
                comicLibComics = ComicLibComics(serverAddress)
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
            return instance.listFiles()
        }

        /**
         * Get a list of the file names in the cache.
         */
        fun getFilesNames(): Array<String>? {
            return instance.list()
        }

        /**
         * Download and cache a comic file.
         * Checks if the file is already cached and downloads it if not.
         * Runs in a new thread.
         */
        fun cacheComicFile(issueID: String) {
            Logging.logDebug("Caching comic file of issue $issueID")
            ComicsCacher(issueID).execute()
        }

        /**
         * Delete a cached comic file.
         * Runs in a new thread.
         */
        fun deleteComicFile(issueID: String) {
            // Check if issueID is empty. If not, continue.
            if (issueID != "") {
                Logging.logDebug("Deleting comic file of issue $issueID")
                ComicsDeleter(issueID).execute()
            }
        }

    }

    /**
     * AsyncTask that runs downloading and caching of comic files in the background.
     */
    class ComicsCacher(private val issueID: String) :
        AsyncTask<String, Int, Unit>() {

        override fun doInBackground(vararg params: String?) {

            init()
            // Check if the file already exists.
            val cachedComicEntity = Database.getInstance().cachedComicsDao().get(issueID)
            if (cachedComicEntity == null) {
                // Not cached. Download and cache.
                val response = comicLibComics.getComicFile(issueID)
                if (response != null) {
                    response.saveFile(getInstance())
                    val cachedComic = CachedComicEntity(
                        issueID,
                        response.filename,
                        CachedComicEntity.isReadable(response.filename)
                    )
                    Database.getInstance().cachedComicsDao()
                        .insert(cachedComic)
                    Logging.logInfo("Download and caching of issue $issueID completed.")
                } else {
                    Logging.logError("Could not download comic file of issue $issueID!")
                }
            } else {
                Logging.logDebug("Comic file of issue $issueID is already cached.")
            }
        }

    }

    /**
     * AsyncTask that runs deletion of cached comic files in the background.
     */
    class ComicsDeleter(private val issueID: String) :
        AsyncTask<String, Int, Unit>() {

        override fun doInBackground(vararg params: String?) {
            val cachedComic = Database.getInstance().cachedComicsDao().get(issueID)
            if (cachedComic != null) {
                // Delete file.
                val comicFile = File(getInstance(), cachedComic.fileName)
                if (comicFile.exists()) {
                    comicFile.delete()
                    // Remove from database.
                    Database.getInstance().cachedComicsDao().delete(cachedComic)
                    Logging.logInfo("Comic file of issue $issueID successfully deleted.")
                } else {
                    Logging.logInfo("Could not delete comic file of issue $issueID! File does not exist.")
                }
            } else {
                Logging.logError("Could not delete comic file of issue $issueID! File is not on database.")
            }
        }

    }

}