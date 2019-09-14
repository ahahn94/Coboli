package de.ahahn94.manhattan.viewModels

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.paging.PagedList
import de.ahahn94.manhattan.model.views.CachedIssuesView
import de.ahahn94.manhattan.repositories.IssueRepo

/**
 * Class that provides the data for the IssuesActivity as a ViewModel.
 * If volumeID is empty, issues will be initialized with the list of all issues.
 * Else, it will be initialized with only the issues of the volumes with the volumeID.
 */
class IssuesViewModel(application: Application, volumeID: String, cachedOnly: Boolean) :
    ViewModel() {

    val issues: LiveData<PagedList<CachedIssuesView>> =
        if (cachedOnly) {
            IssueRepo.getCached(volumeID)
        } else {
            IssueRepo.getAll(volumeID)
        }

    /**
     * Factory to create IssuesViewModel.
     * Necessary to enable additional constructor parameters.
     */
    class Factory(val application: Application, val volumeID: String, val cachedOnly: Boolean) :
        ViewModelProvider.Factory {

        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return IssuesViewModel(application, volumeID, cachedOnly) as T
        }

    }

}

