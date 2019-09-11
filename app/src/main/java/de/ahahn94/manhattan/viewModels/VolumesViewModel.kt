package de.ahahn94.manhattan.viewModels

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.paging.PagedList
import de.ahahn94.manhattan.model.entities.VolumeEntity
import de.ahahn94.manhattan.repositories.VolumeRepo

/**
 * Class that provides the data for the VolumesActivity as a ViewModel.
 * If publisherID is empty, volumes will be initialized with the list of all volumes.
 * Else, it will be initialized with only the volumes of the publisher with the publisherID.
 */
class VolumesViewModel(application: Application, publisherID: String) : ViewModel() {

    val volumes: LiveData<PagedList<VolumeEntity>> = if (publisherID != "") {
        VolumeRepo.getByPublisher(publisherID)
    } else {
        VolumeRepo.getAll()
    }

    /**
     * Factory to create VolumesViewModel.
     * Necessary to enable additional constructor parameters.
     */
    class Factory(val application: Application, val publisherID: String) :
        ViewModelProvider.Factory {

        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return VolumesViewModel(application, publisherID) as T
        }

    }

}