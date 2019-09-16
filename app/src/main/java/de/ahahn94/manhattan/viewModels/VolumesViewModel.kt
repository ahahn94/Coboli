package de.ahahn94.manhattan.viewModels

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.paging.PagedList
import de.ahahn94.manhattan.model.views.CachedVolumesView
import de.ahahn94.manhattan.repositories.VolumeRepo

/**
 * Class that provides the data for the VolumesFragment as a ViewModel.
 * If publisherID is empty, volumes will be initialized with the list of all volumes.
 * Else, it will be initialized with only the volumes of the publisher with the publisherID.
 */
class VolumesViewModel(publisherID: String, cachedOnly: Boolean) :
    ViewModel() {

    val volumes: LiveData<PagedList<CachedVolumesView>> =
        if (publisherID != "") {
            // PublisherID set. Ignore cachedOnly.
            VolumeRepo.getByPublisher(publisherID)
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
        val cachedVolumes: Boolean
    ) :
        ViewModelProvider.Factory {

        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return VolumesViewModel(publisherID, cachedVolumes) as T
        }

    }

}