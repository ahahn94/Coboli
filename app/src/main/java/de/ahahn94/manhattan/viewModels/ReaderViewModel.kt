package de.ahahn94.manhattan.viewModels

import android.os.AsyncTask
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import de.ahahn94.manhattan.cache.ComicsCache
import de.ahahn94.manhattan.livedata.PagesLiveData
import de.ahahn94.manhattan.model.views.CachedIssuesView

/**
 * ViewModel for the ReaderActivity.
 */
class ReaderViewModel(val issue: CachedIssuesView) : ViewModel() {

    // LiveData of the pages of the issue.
    val pages = PagesLiveData(issue)

    /**
     * Default constructor.
     * Load the pages, extracting the comic file if necessary.
     */
    init {
        PagesLoader(pages).execute()
    }

    /**
     * Factory to create ReaderViewModel.
     * Necessary to enable additional constructor parameters.
     */
    class Factory(
        val issue: CachedIssuesView
    ) :
        ViewModelProvider.Factory {

        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return ReaderViewModel(issue) as T
        }

    }

    /**
     * AsyncTask to load the list of filepaths to the pages of the issue.
     * Will extract the files within the comic file of the issue to the comics cache
     * if they have not been extracted yet.
     */
    class PagesLoader(val pagesLiveData: PagesLiveData) : AsyncTask<Unit, Unit, List<String>>() {

        override fun doInBackground(vararg params: Unit?): List<String> {
            if (pagesLiveData.issue.cachedComic?.unpacked != true) {
                ComicsCache.extractComic(pagesLiveData.issue)
            }
            return ComicsCache.getExtractedComic(pagesLiveData.issue.id)
        }

        override fun onPostExecute(result: List<String>) {
            result.forEach {
                pagesLiveData.add(it)
            }
        }

    }

}