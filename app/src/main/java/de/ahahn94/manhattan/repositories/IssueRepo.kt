package de.ahahn94.manhattan.repositories

import android.os.AsyncTask
import androidx.lifecycle.LiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import de.ahahn94.manhattan.model.Database
import de.ahahn94.manhattan.model.ManhattanDatabase
import de.ahahn94.manhattan.model.views.CachedIssuesView
import de.ahahn94.manhattan.utils.Timestamps

/**
 * Repository class for the issues.
 * Handles access to the Issues on the database.
 */
class IssueRepo {
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
         * Get all CachedIssuesView datasets of a volume from the database as an observable PagedList.
         */
        fun getAll(volumeID: String): LiveData<PagedList<CachedIssuesView>> {
            return LivePagedListBuilder(
                getDatabase().issuesDao().getByVolumePaged(volumeID),
                10
            ).build()
        }

        /**
         * Get all CachedIssuesView datasets of a volume.
         */
        fun getCached(volumeID: String): LiveData<PagedList<CachedIssuesView>> {
            return LivePagedListBuilder(
                getDatabase().issuesDao().getCachedByVolume(volumeID),
                10
            ).build()
        }

        /**
         * Update the ReadStatus of an issue on the database.
         */
        fun switchReadStatus(issue: CachedIssuesView) {

            // Get new isRead.
            val newStatus = when (issue.readStatus.isRead) {
                "0" -> "1"
                "1" -> "0"
                else -> "1"
            }
            // Get current UTC timestamp.
            val changed = Timestamps.nowToUtcTimestamp()

            // Update database in background task.
            AsyncTask.execute {
                getDatabase().issuesDao()
                    .updateReadStatus(issue.id, newStatus, issue.readStatus.currentPage, changed)
            }

        }
    }


}