package de.ahahn94.manhattan.asynctasks

import android.os.AsyncTask
import de.ahahn94.manhattan.cache.ComicsCache
import de.ahahn94.manhattan.comicextractors.ExtractorException
import de.ahahn94.manhattan.livedata.PagesLiveData

/**
 * AsyncTask to load the list of filepaths to the pages of the issue.
 * Will extract the files within the comic file of the issue to the comics cache
 * if they have not been extracted yet.
 */
class PagesLoader(
    private val pagesLiveData: PagesLiveData,
    private val thumbnailsLoader: ThumbnailsLoader
) :
    AsyncTask<Unit, Unit, List<String>>() {

    // Set this if an exception occurs while unpacking the comic file.
    lateinit var exception: ExtractorException

    override fun doInBackground(vararg params: Unit?): List<String> {
        if (pagesLiveData.issue.cachedComic?.unpacked != true) {
            try {
                ComicsCache.extractComic(pagesLiveData.issue)
            } catch (e: ExtractorException) {
                // Catch and set the exception.
                // Handling of the exception has to happen in onPostExecute
                // to enable showing Toasts.
                exception = e
                return listOf()
            }
        }
        return ComicsCache.getExtractedComic(pagesLiveData.issue.id)
    }

    override fun onPostExecute(result: List<String>) {
        if (this::exception.isInitialized) {
            // Handle the exception.
            // May show a Toast or throw the exception.
            exception.handleException()
            // Set error so the Activity knows no pages where loaded
            // and can go back to the previous view.
            pagesLiveData.error = true
        } else {
            // Start loading thumbnails.
            thumbnailsLoader.execute()
            // Add pages to pagesLiveData.
            result.forEach {
                pagesLiveData.add(it)
            }
        }
    }

}