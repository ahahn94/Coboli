package de.ahahn94.coboli.livedata

import androidx.lifecycle.LiveData
import de.ahahn94.coboli.model.views.CachedIssuesView

/**
 * Class to provide an CachedIssuesView and the absolute paths
 * to its unpacked pages on the cache as LiveData.
 */
class PagesLiveData(
    val issue: CachedIssuesView
) : LiveData<ArrayList<String>>() {

    // Internal list of the filepaths.
    private val list = arrayListOf<String>()

    // Error status of the caching operation.
    var error : Boolean = false
    set(error: Boolean){
        field = error
        value = list    // Trigger notify.
    }

    /**
     * Add an item to the list and trigger the LiveData to notify
     * all Observers about the change.
     */
    fun add(file: String) {
        list.add(file)
        value = list
    }

}