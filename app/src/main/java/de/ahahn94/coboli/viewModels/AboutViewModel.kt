package de.ahahn94.coboli.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import de.ahahn94.coboli.repositories.AboutRepo
import de.ahahn94.coboli.repositories.AboutRepo.AppInfo
import de.ahahn94.coboli.repositories.AboutRepo.Library

/**
 * Class that provides the data for the AboutFragment as a ViewModel.
 */
class AboutViewModel : ViewModel() {

    // Information about the Coboli app.
    val app = object : MutableLiveData<AppInfo>() {}
    // Information about the libraries and their licenses.
    val libraries = object : MutableLiveData<List<Library>>() {}

    init {
        val about = AboutRepo.getAbout()
        app.postValue(about.appInfo)
        libraries.postValue(about.libraries)
    }

}