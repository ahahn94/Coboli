package de.ahahn94.coboli.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import de.ahahn94.coboli.asynctasks.PagesLoader
import de.ahahn94.coboli.asynctasks.ThumbnailsLoader
import de.ahahn94.coboli.livedata.PagesLiveData
import de.ahahn94.coboli.livedata.ThumbnailsLiveData
import de.ahahn94.coboli.model.views.CachedIssuesView

/**
 * ViewModel for the ReaderActivity.
 */
class ReaderViewModel(val issue: CachedIssuesView) : ViewModel() {

    // LiveData of the pages of the issue.
    val pages = PagesLiveData(issue)

    // LiveData of the thumbnails of the pages.
    val thumbnails = ThumbnailsLiveData(issue)

    // AsyncTask loading the thumbnails. Exposing this to the activity so it can be cancelled
    // if the activity gets destroyed.
    val thumbnailsLoader = ThumbnailsLoader(thumbnails)

    /**
     * Default constructor.
     * Load the pages, extracting the comic file if necessary.
     */
    init {
        PagesLoader(pages, thumbnailsLoader).execute()
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

}