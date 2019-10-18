package de.ahahn94.coboli.repositories

import android.os.AsyncTask
import androidx.lifecycle.LiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import de.ahahn94.coboli.model.Database
import de.ahahn94.coboli.model.CoboliDatabase
import de.ahahn94.coboli.model.views.CachedVolumesView
import de.ahahn94.coboli.utils.Timestamps

/**
 * Repository class for the volumes.
 * Handles access to the Volumes on the database.
 */
class VolumeRepo {
    companion object {

        private lateinit var database: CoboliDatabase

        /**
         * Get the database instance.
         * Initialize it if not yet done.
         */
        private fun getDatabase(): CoboliDatabase {
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
         * from the database as an observable PagedList.
         */
        fun getCached(): LiveData<PagedList<CachedVolumesView>> {
            return LivePagedListBuilder(getDatabase().volumesDao().getCachedPaged(), 10).build()
        }

        /**
         * Get all CachedVolumesView datasets where the name is like searchQuery
         * from the database as an observable PagedList.
         */
        fun getBySearchQuery(query: String): LiveData<PagedList<CachedVolumesView>> {
            return LivePagedListBuilder(
                getDatabase().volumesDao().getBySearchQueryPaged(query),
                10
            ).build()
        }

        /**
         * Update the ReadStatus of a volume on the database.
         */
        fun switchReadStatus(volume: CachedVolumesView, newStatus : ReadStatus) {

            // Get new isRead.
            val newIsRead = when (newStatus) {
                ReadStatus.UNREAD -> false
                ReadStatus.READ -> true
            }

            // Get current UTC timestamp.
            val changed = Timestamps.nowToUtcTimestamp()

            // Update database in background task.
            AsyncTask.execute {
                database.volumesDao().updateReadStatus(volume.id, newIsRead, changed)
            }

        }
    }

    /**
     * Enum class for the readStatus of volumes.
     */
    enum class ReadStatus{
        UNREAD,
        READ
    }

}