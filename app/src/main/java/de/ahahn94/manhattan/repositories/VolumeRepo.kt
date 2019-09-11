package de.ahahn94.manhattan.repositories

import android.os.AsyncTask
import androidx.lifecycle.LiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import de.ahahn94.manhattan.model.Database
import de.ahahn94.manhattan.model.ManhattanDatabase
import de.ahahn94.manhattan.model.entities.VolumeEntity
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
         * Get all VolumeEntity datasets from the database as an observable PagedList.
         */
        fun getAll(): LiveData<PagedList<VolumeEntity>> {
            return LivePagedListBuilder(getDatabase().volumesDao().getAllPaged(), 10).build()
        }

        /**
         * Get a single VolumeEntity as LiveData.
         */
        fun get(volumeID: String): LiveData<VolumeEntity> {
            return getDatabase().volumesDao().getLiveData(volumeID)
        }

        /**
         * Get all VolumeEntity datasets of a publisher from the database as an observable PagedList.
         */
        fun getByPublisher(publisherID: String): LiveData<PagedList<VolumeEntity>> {
            return LivePagedListBuilder(
                getDatabase().volumesDao().getByPublisherPaged(publisherID),
                10
            ).build()
        }

        /**
         * Update the ReadStatus of a volume on the database.
         */
        fun switchReadStatus(volumeEntity: VolumeEntity) {

            // Get new isRead.
            val newStatus = when (volumeEntity.readStatus?.isRead ?: "0") {
                "0" -> "1"
                "1" -> "0"
                else -> "1"
            }
            // Get current UTC timestamp.
            val changed = Timestamps.nowToUtcTimestamp()

            // Update database in background task.
            AsyncTask.execute {
                database.volumesDao().updateReadStatus(volumeEntity.id, newStatus, changed)
            }

        }
    }


}