package de.ahahn94.coboli.repositories

import androidx.lifecycle.LiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import de.ahahn94.coboli.model.Database
import de.ahahn94.coboli.model.CoboliDatabase
import de.ahahn94.coboli.model.entities.PublisherEntity

/**
 * Repository class for the publishers.
 * Handles access to the Publishers on the database.
 */
class PublisherRepo {

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
         * Get all PublisherEntity datasets from the database as an observable PagedList.
         */
        fun getAll(): LiveData<PagedList<PublisherEntity>> {
            return LivePagedListBuilder(getDatabase().publishersDao().getAllPaged(), 10).build()
        }

        /**
         * Get a single PublisherEntity as LiveData.
         */
        fun get(publisherID: String): LiveData<PublisherEntity> {
            return getDatabase().publishersDao().getLiveData(publisherID)
        }

    }

}