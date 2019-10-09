package de.ahahn94.coboli.repositories

import android.os.AsyncTask
import androidx.lifecycle.LiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import de.ahahn94.coboli.model.Database
import de.ahahn94.coboli.model.CoboliDatabase
import de.ahahn94.coboli.model.views.CachedIssuesView
import de.ahahn94.coboli.utils.Timestamps

/**
 * Repository class for the issues.
 * Handles access to the Issues on the database.
 */
class IssueRepo {
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
         * Get all CachedIssuesView datasets where isRead == false and currentPage != 0.
         */
        fun getReadingList(): LiveData<PagedList<CachedIssuesView>> {
            return LivePagedListBuilder(
                getDatabase().issuesDao().getReading(),
                10
            ).build()
        }


        /**
         * Update the ReadStatus of an issue on the database.
         */
        fun switchReadStatus(issue: CachedIssuesView, newStatus: ReadStatus) {

            // Get new isRead.
            val newIsRead = when (newStatus) {
                ReadStatus.UNREAD -> false
                ReadStatus.IN_PROGRESS -> false
                ReadStatus.READ -> true
            }

            // Get new currentPage.
            val newCurrentPage = when(newStatus){
                ReadStatus.UNREAD -> 0
                ReadStatus.IN_PROGRESS -> when (issue.readStatus.currentPage){
                    0 -> 1
                    else -> issue.readStatus.currentPage
                }
                ReadStatus.READ -> issue.readStatus.currentPage
            }

            // Get current UTC timestamp.
            val changed = Timestamps.nowToUtcTimestamp()

            // Update database in background task.
            AsyncTask.execute {
                getDatabase().issuesDao()
                    .updateReadStatus(issue.id, newIsRead, newCurrentPage, changed)
            }

        }
    }

    /**
     * Enum class for the readStatus of issues.
     */
    enum class ReadStatus {
        UNREAD,
        IN_PROGRESS,
        READ
    }

}