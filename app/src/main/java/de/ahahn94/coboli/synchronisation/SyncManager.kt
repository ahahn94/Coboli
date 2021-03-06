package de.ahahn94.coboli.synchronisation

import android.content.Context
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.work.*
import de.ahahn94.coboli.api.repos.ComicLibAPI
import de.ahahn94.coboli.cache.ComicsCache
import de.ahahn94.coboli.cache.ImagesCache
import de.ahahn94.coboli.model.Database
import de.ahahn94.coboli.model.CoboliDatabase
import de.ahahn94.coboli.model.entities.IssueEntity
import de.ahahn94.coboli.model.entities.PublisherEntity
import de.ahahn94.coboli.model.entities.VolumeEntity
import de.ahahn94.coboli.utils.ContextProvider
import de.ahahn94.coboli.utils.Logging
import de.ahahn94.coboli.utils.Timestamps
import de.ahahn94.coboli.utils.replaceNull
import de.ahahn94.coboli.utils.settings.Preferences

/**
 * Class that handles the synchronization between the ComicLib API and the local database.
 */
class SyncManager {

    companion object {

        lateinit var comicLibApi: ComicLibAPI
        lateinit var database: CoboliDatabase

        // Tag for the SyncManager job.
        // Used to make sure that a) only one sync task is run at a time and b) the job is resumed
        // if it was paused by quitting the app.
        private const val SYNC_MANAGER_WORK_TAG = "CoboliSyncManager"

        /**
         * Initialize comicLibApi, and database if necessary.
         */
        fun init() {
            comicLibApi = ComicLibAPI(
                Preferences.getInstance().getString(
                    Preferences.SERVER_ADDRESS_KEY,
                    ""
                ) replaceNull ""
            )

            if (!this::database.isInitialized) {
                database = Database.getInstance()
            }
        }

        /**
         * Initialize for unit tests.
         * Initialize comicLibApi if necessary.
         * Initialize database with a non-persistent InMemoryDatabase.
         * Use this init-function for unit tests.
         */
        fun initForTest() {
            if (!this::comicLibApi.isInitialized) {
                comicLibApi = ComicLibAPI(
                    Preferences.getInstance().getString(
                        Preferences.SERVER_ADDRESS_KEY,
                        ""
                    ) replaceNull ""
                )
                database = Database.getInMemoryDatabase()
            }
        }

        /**
         * Sync the local collection with the ComicLib server:
         * - Update the local collection with the datasets from the ComicLib API.
         * - Update the ReadStatus on the ComicLib API if the local one is newer.
         */
        fun startSync() {

            // Initialize comicLibApi and database.
            init()
            // Initialize the ImagesCache.
            ImagesCache.init()

            Logging.logDebug("Starting synchronisation.")

            // Get publishers, volumes and issues from ComicLib API.
            val publishersFromApiResponse = comicLibApi.getPublishers()
            val volumesFromApiResponse = comicLibApi.getVolumes()
            val issuesFromApiResponse = comicLibApi.getIssues()

            // Do not proceed if error getting one of the resources.
            if (
                !publishersFromApiResponse.isSuccessful or
                !volumesFromApiResponse.isSuccessful or
                !issuesFromApiResponse.isSuccessful
            ) {
                Logging.logError("Error reading from ComicLib API! Aborting sync!"); return
            }

            // Do not proceed if empty content on one of the responses.
            if (
                (publishersFromApiResponse.body()!!.responseContent == null) or
                (volumesFromApiResponse.body()!!.responseContent == null) or
                (issuesFromApiResponse.body()!!.responseContent == null)
            ) {
                Logging.logError("Error reading from ComicLib API! Aborting sync!"); return
            }

            // Get content from responses. Throw exception if null.
            val publishersFromApi = publishersFromApiResponse.body()!!.responseContent!!
            val volumesFromApi = volumesFromApiResponse.body()!!.responseContent!!
            val issuesFromApi = issuesFromApiResponse.body()!!.responseContent!!

            syncPublishers(publishersFromApi)

            syncVolumes(volumesFromApi)

            syncIssues(issuesFromApi)

            Logging.logDebug("Synchronisation finished.")
        }


        /**
         * Sync publishers.
         * - Get publishers from database.
         * - Delete publishers that are missing on the ComicLib API (deleted by server admin).
         * - Update publishers that are on both lists.
         * - Add publishers that are missing on the database.
         */
        private fun syncPublishers(fromApi: List<PublisherEntity>) {
            val fromDatabase = database.publishersDao().getAll()
            val mapFromDatabase = fromDatabase.map { item -> item.id to item }.toMap()
            val keysFromDatabase = mapFromDatabase.keys
            val mapFromApi = fromApi.map { item -> item.id to item }.toMap()
            val keysFromApi = mapFromApi.keys
            val keysNotOnApi = keysFromDatabase.minus(keysFromApi)
            val keysNotOnDatabase = keysFromApi.minus(keysFromDatabase)
            val keysOnBoth = keysFromApi.intersect(keysFromDatabase)

            // Get the datasets matching the keys from the keysNotOnDatabase list. Add them to the database.
            val datasetsNotOnDatabase =
                mapFromApi.filterKeys { key -> keysNotOnDatabase.contains(key) }.values.toList()
            addPublishers(datasetsNotOnDatabase)

            // Get the datasets matching the keys from the keysOnBoth list. Update them on the database.
            val datasetsOnBoth =
                mapFromApi.filterKeys { key -> keysOnBoth.contains(key) }.values.toList()
            updatePublishers(datasetsOnBoth)

            // Get the datasets matching the keys from the keysNotOnApi list. Remove them from the database.
            val datasetsNotOnApi =
                mapFromDatabase.filterKeys { key -> keysNotOnApi.contains(key) }.values.toList()
            deletePublishers(datasetsNotOnApi)
        }

        /**
         * Sync volumes.
         * - Get volumes from database.
         * - Delete volumes that are missing on the ComicLib API (deleted by server admin).
         * - Update volumes that are on both lists.
         * - Sync volumes ReadStatus with ComicLib API.
         * - Add volumes that are missing on the database.
         */
        private fun syncVolumes(fromApi: List<VolumeEntity>) {
            val fromDatabase = database.volumesDao().getAll()
            val mapFromDatabase = fromDatabase.map { item -> item.id to item }.toMap()
            val keysFromDatabase = mapFromDatabase.keys
            val mapFromApi = fromApi.map { item -> item.id to item }.toMap()
            val keysFromApi = mapFromApi.keys
            val keysNotOnApi = keysFromDatabase.minus(keysFromApi)
            val keysNotOnDatabase = keysFromApi.minus(keysFromDatabase)
            val keysOnBoth = keysFromApi.intersect(keysFromDatabase)

            // Get the datasets matching the keys from the keysNotOnDatabase list. Add them to the database.
            val datasetsNotOnDatabase =
                mapFromApi.filterKeys { key -> keysNotOnDatabase.contains(key) }.values.toList()
            addVolumes(datasetsNotOnDatabase)

            // Get the datasets matching the keys from the keysOnBoth list. Update them on the database.
            // Sync ReadStatus with API.
            val datasetsOnBoth =
                mapFromApi.filterKeys { key -> keysOnBoth.contains(key) }.values.toList()
            updateVolumes(datasetsOnBoth)

            // Get the datasets matching the keys from the keysNotOnApi list. Remove them from the database.
            val datasetsNotOnApi =
                mapFromDatabase.filterKeys { key -> keysNotOnApi.contains(key) }.values.toList()
            deleteVolumes(datasetsNotOnApi)
        }

        /**
         * Sync issues.
         * - Get issues from database.
         * - Delete issues that are missing on the ComicLib API (deleted by server admin).
         * - Update issues that are on both lists.
         * - Add issues that are missing on the database.
         */
        private fun syncIssues(fromApi: List<IssueEntity>) {
            val fromDatabase = database.issuesDao().getAll()
            val mapFromDatabase = fromDatabase.map { item -> item.id to item }.toMap()
            val keysFromDatabase = mapFromDatabase.keys
            val mapFromApi = fromApi.map { item -> item.id to item }.toMap()
            val keysFromApi = mapFromApi.keys
            val keysNotOnApi = keysFromDatabase.minus(keysFromApi)
            val keysNotOnDatabase = keysFromApi.minus(keysFromDatabase)
            val keysOnBoth = keysFromApi.intersect(keysFromDatabase)

            // Get the datasets matching the keys from the keysNotOnDatabase list. Add them to the database.
            val datasetsNotOnDatabase =
                mapFromApi.filterKeys { key -> keysNotOnDatabase.contains(key) }.values.toList()
            addIssues(datasetsNotOnDatabase)

            // Get the datasets matching the keys from the keysOnBoth list. Update them on the database.
            val datasetsOnBoth =
                mapFromApi.filterKeys { key -> keysOnBoth.contains(key) }.values.toList()
            updateIssues(datasetsOnBoth)

            // Get the datasets matching the keys from the keysNotOnApi list. Remove them from the database.
            val datasetsNotOnApi =
                mapFromDatabase.filterKeys { key -> keysNotOnApi.contains(key) }.values.toList()
            deleteIssues(datasetsNotOnApi)
        }

        /**
         * Add publishers to the local collection:
         * - Add publishers to database.
         * - Cache publisher image.
         */
        private fun addPublishers(publisherEntities: List<PublisherEntity>) {
            publisherEntities.forEach {
                database.publishersDao().insert(it)
                ImagesCache.cacheImageFile(it.imageFileURL)
            }
        }

        /**
         * Update the publishers with the data from the ComicLib API.
         */
        private fun updatePublishers(publisherEntities: List<PublisherEntity>) {
            // PublishersDao.update can take an array as a vararg, so using the spread-operator *.
            database.publishersDao().update(*publisherEntities.toTypedArray())
        }

        /**
         * Delete publishers from the local collection:
         * - Remove the issues of the publisher including their comic and image files.
         * - Remove the volumes of the publisher including their image files.
         * - Remove the image file of the publisher.
         * - Remove the publishers.
         */
        private fun deletePublishers(publisherEntities: List<PublisherEntity>) {
            publisherEntities.forEach {
                deleteVolumes(database.volumesDao().getByPublisher(it.id).toList())
                ImagesCache.deleteImage(it.imageFileURL)
                database.publishersDao().delete(it)
            }
        }

        /**
         * Add volumes to the local collection:
         * - Add volumes to database.
         * - Cache volume image.
         */
        private fun addVolumes(volumeEntities: List<VolumeEntity>) {
            volumeEntities.forEach {
                database.volumesDao().insert(it)
                ImagesCache.cacheImageFile(it.imageFileURL)
            }
        }

        /**
         * Update the volumes with the data from the ComicLib API.
         */
        private fun updateVolumes(volumeEntities: List<VolumeEntity>) {
            // ReadStatus is generated via a view in ComicLib as well as Coboli. No sync necessary.
            // VolumesDao.update can take an array as a vararg, so using the spread-operator *.
            database.volumesDao().update(*volumeEntities.toTypedArray())
        }

        /**
         * Remove volumes from the local collection:
         * - Remove the issues of the volume and their comic and image files.
         * - Remove the image of the volume.
         * - Remove the volume from the database.
         */
        private fun deleteVolumes(volumeEntities: List<VolumeEntity>) {
            volumeEntities.forEach {
                deleteIssues(database.issuesDao().getByVolume(it.id).toList())
                ImagesCache.deleteImage(it.imageFileURL)
                database.volumesDao().delete(it)
            }
        }

        /**
         * Add issues to the local collection:
         * - Add issues to database.
         * - Cache issue image.
         */
        private fun addIssues(issueEntities: List<IssueEntity>) {
            issueEntities.forEach {
                database.issuesDao().insert(it)
                ImagesCache.cacheImageFile(it.imageFileURL)
            }
        }

        /**
         * Update the issues with the data from the ComicLib API.
         * Sync the ReadStatus with the API.
         */
        private fun updateIssues(issueEntities: List<IssueEntity>) {
            issueEntities.forEach {
                // Compare ReadStatus timestamp and update API and database.
                val onDatabase = database.issuesDao().get(it.id)
                if (onDatabase != null) {
                    val changedOnDatabase =
                        Timestamps.timeStampToDate(onDatabase.readStatus.timestampChanged)
                    val changedOnApi = Timestamps.timeStampToDate(it.readStatus.timestampChanged)
                    val difference = changedOnDatabase?.compareTo(changedOnApi)
                    if (difference != null) {
                        it.readStatus = when {
                            // Timestamp from API is newer. Update on database.
                            difference < 0 -> it.readStatus
                            // Timestamps are equal. Update on database.
                            difference == 0 -> it.readStatus
                            // Timestamp from database is newer. Update on API.
                            else -> {
                                comicLibApi.putIssueReadStatus(
                                    onDatabase.id,
                                    onDatabase.readStatus
                                )
                                onDatabase.readStatus
                            }
                        }
                    }
                }
            }

            // IssuesDao.update can take an array as a vararg, so using the spread-operator *.
            database.issuesDao().update(*issueEntities.toTypedArray())
        }

        /**
         * Delete issues from the local collection:
         * - Remove the cached comics file of the issue (if it exists).
         * - Remove the cached image file of the issue.
         * - Remove the issue from the database.
         */
        private fun deleteIssues(issueEntities: List<IssueEntity>) {
            issueEntities.forEach {
                ComicsCache.deleteComicFile(it.id)
                ImagesCache.deleteImage(it.imageFileURL)
                database.issuesDao().delete(it)
            }
        }

        /**
         * Run the startSync-function in the background.
         * Runs callback function after background job is done.
         */
        fun runSyncInBackground(owner: LifecycleOwner, callback: () -> Unit) {
            val job = OneTimeWorkRequest.Builder(SyncWorker::class.java).build()
            WorkManager.getInstance(ContextProvider.getApplicationContext()).enqueueUniqueWork(
                SYNC_MANAGER_WORK_TAG, ExistingWorkPolicy.KEEP, job
            )

            // Register callback for end of job.
            WorkManager.getInstance(ContextProvider.getApplicationContext())
                .getWorkInfosForUniqueWorkLiveData(
                    SYNC_MANAGER_WORK_TAG
                ).observe(owner, Observer {
                    if (it.isNotEmpty()) {
                        if (it.first()?.state == WorkInfo.State.SUCCEEDED) {
                            callback()
                        }
                    }
                })
        }

    }

    /**
     * Worker that runs the startSync-function in the background.
     */
    class SyncWorker(
        context: Context, params: WorkerParameters
    ) : Worker(context, params) {

        override fun doWork(): Result {
            startSync()
            return Result.success()
        }

    }

}