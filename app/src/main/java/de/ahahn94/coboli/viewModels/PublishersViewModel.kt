package de.ahahn94.coboli.viewModels

import androidx.lifecycle.ViewModel
import de.ahahn94.coboli.repositories.PublisherRepo

/**
 * Class that provides the data for the PublishersFragment as a ViewModel.
 */
class PublishersViewModel : ViewModel() {

    val publishers = PublisherRepo.getAll()

}