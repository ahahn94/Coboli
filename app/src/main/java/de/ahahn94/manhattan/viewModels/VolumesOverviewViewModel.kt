package de.ahahn94.manhattan.viewModels

import androidx.lifecycle.ViewModel
import de.ahahn94.manhattan.repositories.VolumeRepo

/**
 * Class that provides the data for the VolumesOverviewActivity as a ViewModel.
 */
class VolumesOverviewViewModel : ViewModel() {

    val volumes = VolumeRepo.getAll()

}