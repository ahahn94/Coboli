package de.ahahn94.manhattan.viewModels

import androidx.lifecycle.ViewModel
import de.ahahn94.manhattan.repositories.PublisherRepo

/**
 * Class that provides the data for the PublishersFragment as a ViewModel.
 */
class PublishersViewModel : ViewModel() {

    val publishers = PublisherRepo.getAll()

}