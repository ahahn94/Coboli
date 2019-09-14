package de.ahahn94.manhattan.repositories

import android.os.AsyncTask
import androidx.lifecycle.LiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import de.ahahn94.manhattan.model.Database
import de.ahahn94.manhattan.model.ManhattanDatabase
import de.ahahn94.manhattan.model.views.CachedVolumesView
import de.ahahn94.manhattan.utils.Timestamps

/**
 * Repository class for the volumes.
 * Handles access to the Volumes on the database.
 */
class VolumeRepo {
    companion object {

        private lateinit var database: ManhattanDatabase

        /**
         * Get the database instance.
         * Initialize it if not yet done.
         */
        private fun getDatabase(): ManhattanDatabase {
            if (!this::database.isInitialized) {
                database = Database.getInstance()
            }
            return database
        }

        /**
         * Get all CachedVolumesView datasets from the database as an observable PagedList.
         */
        fun getAll(): LiveData<PagedList<CachedVolumesView>> {
            return LivePagedListBuilder(getDatabase().volumesDao().getAllPaged(), 10).build()
        }

        /**
         * Get all CachedVolumesView datasets of a publisher from the database as an observable PagedList.
         */
        fun getByPublisher(publisherID: String): LiveData<PagedList<CachedVolumesView>> {
            return LivePagedListBuilder(
                getDatabase().volumesDao().getByPublisherPaged(publisherID),
                10
            ).build()
        }

        /**
         * Get all CachedVolumesView datasets that have cached comics
         * from the database as anobservable PagedList.
         */
        fun getCached(): LiveData<PagedList<CachedVolumesView>> {
            return LivePagedListBuilder(getDatabase().volumesDao().getCachedPaged(), 10).build()
        }

        /**
         * Update the ReadStatus of a volume on the database.
         */
        fun switchReadStatus(volume: CachedVolumesView) {

            // Get new isRead.
            val newStatus = when (volume.readStatus?.isRead ?: "0") {
                "0" -> "1"
                "1" -> "0"
                else -> "1"
            }
            // Get current UTC timestamp.
            val changed = Timestamps.nowToUtcTimestamp()

            // Update database in background task.
            AsyncTask.execute {
                database.volumesDao().updateReadStatus(volume.id, newStatus, changed)
            }

        }
    }


}