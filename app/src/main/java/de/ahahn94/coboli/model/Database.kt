package de.ahahn94.coboli.model

import androidx.room.Room
import de.ahahn94.coboli.utils.ContextProvider

/**
 * Class to provide a singleton instance of the apps database.
 */
class Database {

    companion object {

        // Singleton instance of the database.
        private lateinit var instance: CoboliDatabase

        /**
         * Get the instance of the database.
         * Initialize it if necessary.
         */
        fun getInstance(): CoboliDatabase {
            if (!this::instance.isInitialized) {
                instance = Room.databaseBuilder(
                    ContextProvider.getApplicationContext(),
                    CoboliDatabase::class.java,
                    "Coboli"
                ).build()
            }
            return instance
        }

        /**
         * Get an in-memory version of the database for unit tests.
         * THIS WILL NOT BE PERSISTENT!
         */
        fun getInMemoryDatabase(): CoboliDatabase {
            return Room.inMemoryDatabaseBuilder(
                ContextProvider.getApplicationContext(),
                CoboliDatabase::class.java
            ).build()
        }

    }

}
