package de.ahahn94.manhattan.database

import androidx.room.Database
import androidx.room.RoomDatabase

/**
 * RoomDatabase that wraps the tables and DAOs for the app database.
 */
@Database(entities = [Publisher::class, Volume::class, Issue::class], version = 1)
abstract class ManhattanDatabase : RoomDatabase() {
    abstract fun issuesDao(): Issue.IssuesDao
    abstract fun publishersDao(): Publisher.PublishersDao
    abstract fun volumesDao(): Volume.VolumesDao
}