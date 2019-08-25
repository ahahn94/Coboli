package de.ahahn94.manhattan.database

import androidx.room.Room
import de.ahahn94.manhattan.utils.ContextProvider

/**
 * Class to provide a singleton instance of the apps database.
 */
class Database {

    companion object {

        // Singleton instance of the database.
        private lateinit var instance: ManhattanDatabase

        /**
         * Get the instance of the database.
         * Initialize it if necessary.
         */
        fun getInstance(): ManhattanDatabase {
            if (!this::instance.isInitialized) {
                instance = Room.databaseBuilder(
                    ContextProvider.getApplicationContext(),
                    ManhattanDatabase::class.java,
                    "Manhattan"
                ).build()
            }
            return instance
        }

        /**
         * Get an in-memory version of the database for unit tests.
         * THIS WILL NOT BE PERSISTENT!
         */
        fun getInMemoryDatabase(): ManhattanDatabase {
            return Room.inMemoryDatabaseBuilder(
                ContextProvider.getApplicationContext(),
                ManhattanDatabase::class.java
            ).build()
        }

    }

}
