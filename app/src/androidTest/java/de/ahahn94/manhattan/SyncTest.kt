package de.ahahn94.manhattan

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import de.ahahn94.manhattan.api.repos.ComicLibAPI
import de.ahahn94.manhattan.database.ManhattanDatabase
import de.ahahn94.manhattan.synchronisation.SyncManager
import de.ahahn94.manhattan.utils.ContextProvider
import de.ahahn94.manhattan.utils.Logging
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Unit test for the SyncManager functions.
 */
@RunWith(AndroidJUnit4::class)
class SyncTest {

    companion object {

        // Constants for testing. IDs taken from the example datasets of ComicLib.
        private const val ISSUE_ID = "511011"
        private const val VOLUME_ID = "87312"
        private const val PUBLISHER_ID = "4212"

        // Fake ids for testing of the delete-functions.
        private const val FAKE_ISSUE_ID = "123"
        private const val FAKE_VOLUME_ID = "456"
        private const val FAKE_PUBLISHER_ID = "789"

        // Old timestamp for testing of the update function.
        private const val OLD_TIMESTAMP = "2000-01-01 00:00:00"

        private lateinit var database: ManhattanDatabase
        private lateinit var comicLibAPI: ComicLibAPI

    }

    /**
     * Initialize ContextProvider, SyncManager, Database and ComicLibApi for the tests.
     */
    init {
        ContextProvider.setApplicationContext(InstrumentationRegistry.getInstrumentation().targetContext)
        SyncManager.initForTest()
        database = SyncManager.database
        comicLibAPI = SyncManager.comicLibApi
    }

    /**
     * Prepare the Publishers table with datasets for testing.
     * One fake dataset for deletion and one manipulated for updating.
     */
    @Before
    fun preparePublishers() {
        val publisher = comicLibAPI.getPublisher(PUBLISHER_ID).body()?.responseContent
        if (publisher != null) {
            // Insert publisher for update testing.
            publisher.name = ""
            database.publishersDao().insert(publisher)
            // Insert publisher for delete testing
            publisher.id = FAKE_PUBLISHER_ID
            database.publishersDao().insert(publisher)
        }
    }

    /**
     * Prepare the Volumes table with datasets for testing.
     * One fake dataset for deletion and one manipulated for updating.
     */
    @Before
    fun prepareVolumes() {
        val volume = comicLibAPI.getVolume(VOLUME_ID).body()?.responseContent
        if (volume != null) {
            // Insert volume for update testing.
            volume.name = ""
            database.volumesDao().insert(volume)
            // Insert volume for delete testing
            volume.id = FAKE_VOLUME_ID
            database.volumesDao().insert(volume)
        }
    }

    /**
     * Prepare the Issues table with datasets for testing.
     * One fake dataset for deletion and one manipulated for updating.
     */
    @Before
    fun prepareIssues() {
        val issue = comicLibAPI.getIssue(ISSUE_ID).body()?.responseContent
        if (issue != null) {
            // Insert issue for update testing.
            issue.readStatus.timestampChanged = OLD_TIMESTAMP
            database.issuesDao().insert(issue)
            // Insert issue for delete testing
            issue.id = FAKE_ISSUE_ID
            database.issuesDao().insert(issue)
        }
    }

    /**
     * Run all tests.
     */
    @Test
    fun test() {
        Logging.logDebug("Starting SyncTest")
        SyncManager.startSync()
        testPublishers()
        testVolumes()
        testIssues()
        Logging.logDebug("Done")
    }


    /**
     * Test if the update, insert and delete actions on the Publishers table were successful.
     */
    private fun testPublishers() {
        // Check that the publisher was updated via the change on the name field.
        assertTrue(database.publishersDao().get(PUBLISHER_ID)?.name != "")
        // Check that the faked publisher was remove from the database.
        assertTrue(database.publishersDao().get(FAKE_PUBLISHER_ID) == null)
        // Check that both publishers from the example datasets of the ComicLib API have been added.
        assertTrue(database.publishersDao().getAll().size == 2)
    }

    /**
     * Test if the update, insert and delete actions on the Volumes table were successful.
     */
    private fun testVolumes() {
        // Check that the volume was updated via the change on the timestampChanged field.
        assertTrue(database.volumesDao().get(VOLUME_ID)?.name != "")
        // Check that the faked volume was remove from the database.
        assertTrue(database.volumesDao().get(FAKE_VOLUME_ID) == null)
        // Check that all volumes from the example datasets of the ComicLib API have been added.
        assertTrue(database.volumesDao().getAll().size == 21)
    }

    /**
     * Test if the update, insert and delete actions on the Issues table were successful.
     */
    private fun testIssues() {
        // Check that the issue was updated via the change on the timestampChanged field.
        assertTrue(database.issuesDao().get(ISSUE_ID)?.readStatus?.timestampChanged != OLD_TIMESTAMP)
        // Check that the faked issue was remove from the database.
        assertTrue(database.issuesDao().get(FAKE_ISSUE_ID) == null)
        // Check that all issues from the example datasets of the ComicLib API have been added.
        assertTrue(database.issuesDao().getAll().size == 23)
    }

}