package de.ahahn94.manhattan.repositories

import androidx.lifecycle.LiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import de.ahahn94.manhattan.model.Database
import de.ahahn94.manhattan.model.ManhattanDatabase
import de.ahahn94.manhattan.model.entities.VolumeEntity

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

    }

}