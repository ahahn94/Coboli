package de.ahahn94.manhattan.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.paging.PagedList
import de.ahahn94.manhattan.model.views.CachedVolumesView
import de.ahahn94.manhattan.repositories.VolumeRepo

/**
 * Class that provides the data for the VolumesFragment as a ViewModel.
 * If publisherID is not empty, volumes will be initialized with only the volumes of
 * the publisher with the publisherID.
 * If searchQuery is not empty, volumes will be initialized with the result of a search
 * for the volumes with a name like searchQuery.
 * If cachedOnly is true, volumes will be initialized with a list of the volumes that have cached
 * issues.
 * If none of the former is applicable, volumes will be initialized with a list of all volumes
 * on the database.
 */
class VolumesViewModel(publisherID: String, cachedOnly: Boolean, searchQuery: String) :
    ViewModel() {

    val volumes: LiveData<PagedList<CachedVolumesView>> =
        if (publisherID != "") {
            // PublisherID set. Ignore cachedOnly.
            VolumeRepo.getByPublisher(publisherID)
        } else if (searchQuery != "") {
            VolumeRepo.getBySearchQuery(searchQuery)
        } else {
            if (cachedOnly == true) {
                VolumeRepo.getCached()
            } else {
                VolumeRepo.getAll()
            }
        }

    /**
     * Factory to create VolumesViewModel.
     * Necessary to enable additional constructor parameters.
     */
    class Factory(
        val publisherID: String,
        val cachedVolumes: Boolean,
        val searchQuery: String
    ) :
        ViewModelProvider.Factory {

        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return VolumesViewModel(publisherID, cachedVolumes, searchQuery) as T
        }

    }

}