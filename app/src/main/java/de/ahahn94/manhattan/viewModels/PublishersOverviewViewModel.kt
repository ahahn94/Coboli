package de.ahahn94.manhattan.viewModels

import androidx.lifecycle.ViewModel
import de.ahahn94.manhattan.repositories.PublisherRepo

/**
 * Class that provides the data for the PublishersOverviewActivity as a ViewModel.
 */
class PublishersOverviewViewModel : ViewModel() {

    val publishers = PublisherRepo.getAll()

}