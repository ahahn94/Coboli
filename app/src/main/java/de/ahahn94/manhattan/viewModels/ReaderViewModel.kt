package de.ahahn94.manhattan.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import de.ahahn94.manhattan.asynctasks.PagesLoader
import de.ahahn94.manhattan.asynctasks.ThumbnailsLoader
import de.ahahn94.manhattan.livedata.PagesLiveData
import de.ahahn94.manhattan.livedata.ThumbnailsLiveData
import de.ahahn94.manhattan.model.views.CachedIssuesView

/**
 * ViewModel for the ReaderActivity.
 */
class ReaderViewModel(val issue: CachedIssuesView) : ViewModel() {

    // LiveData of the pages of the issue.
    val pages = PagesLiveData(issue)

    // LiveData of the thumbnails of the pages.
    val thumbnails = ThumbnailsLiveData(issue)

    /**
     * Default constructor.
     * Load the pages, extracting the comic file if necessary.
     */
    init {
        PagesLoader(pages, ThumbnailsLoader(thumbnails)).execute()
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