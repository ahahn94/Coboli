package de.ahahn94.coboli

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import de.ahahn94.coboli.api.repos.ComicLibAPI
import de.ahahn94.coboli.model.Database
import de.ahahn94.coboli.model.CoboliDatabase
import de.ahahn94.coboli.utils.ContextProvider
import de.ahahn94.coboli.utils.Logging
import de.ahahn94.coboli.utils.replaceNull
import de.ahahn94.coboli.utils.settings.Preferences
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Unit test for the database functions.
 */
@RunWith(AndroidJUnit4::class)
class DatabaseTest {

    companion object {

        // Constants for testing. IDs taken from the example datasets of ComicLib.
        private const val ISSUE_ID = "511011"
        private const val VOLUME_ID = "87312"
        private const val PUBLISHER_ID = "4212"

        private lateinit var database: CoboliDatabase
        private lateinit var comicLibAPI: ComicLibAPI

    }

    /**
     * Constructor.
     * Initialize comicLibAPI and database.
     */
    init {
        ContextProvider.setApplicationContext(InstrumentationRegistry.getInstrumentation().targetContext)
        val serverAddress =
            Preferences.getInstance().getString(Preferences.SERVER_ADDRESS_KEY, "") replaceNull ""
        comicLibAPI = ComicLibAPI(serverAddress)
        database = Database.getInMemoryDatabase()
    }

    /**
     * Run all tests.
     */
    @Test
    fun test() {
        Logging.logDebug("Starting DatabaseTest")
        testPublishers()
        testVolumes()
        testIssues()
        Logging.logDebug("Done")
    }

    /**
     * Run tests on the Publishers table.
     */
    private fun testPublishers() {
        val publisher = comicLibAPI.getPublisher(PUBLISHER_ID).body()?.responseContent

        if (publisher != null) {
            database.publishersDao().insert(publisher)

            val fromDatabase = database.publishersDao().getAll()
            assertTrue(fromDatabase.isNotEmpty())
        }
    }

    /**
     * Run tests on the Volumes table.
     */
    private fun testVolumes() {
        val volume = comicLibAPI.getVolume(VOLUME_ID).body()?.responseContent

        if (volume != null) {
            database.volumesDao().insert(volume)

            val fromDatabase = database.volumesDao().getAll()
            assertTrue(fromDatabase.isNotEmpty())

            // Check that a ReadStatus was loaded from the VolumeReadStatus view.
            assertTrue(fromDatabase.first().readStatus != null)
        }
    }

    /**
     * Run tests on the Issues table.
     */
    private fun testIssues() {
        val issue = comicLibAPI.getIssue(ISSUE_ID).body()?.responseContent

        if (issue != null) {
            database.issuesDao().insert(issue)

            val fromDatabase = database.issuesDao().getAll()
            assertTrue(fromDatabase.isNotEmpty())
        }
    }
}