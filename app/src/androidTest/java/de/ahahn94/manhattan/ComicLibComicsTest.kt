package de.ahahn94.manhattan


import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import de.ahahn94.manhattan.api.repos.ComicLibComics
import de.ahahn94.manhattan.cache.ComicsCache
import de.ahahn94.manhattan.utils.ContextProvider
import de.ahahn94.manhattan.utils.Logging
import de.ahahn94.manhattan.utils.replaceNull
import de.ahahn94.manhattan.utils.settings.Preferences
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Unit tests for the functions that handle the comic file parts of the ComicLib API.
 */
@RunWith(AndroidJUnit4::class)
class ComicLibComicsTest {

    companion object {

        // Constants for testing. ID taken from the example datasets of ComicLib..
        private const val ISSUE_ID = "511011"
    }

    // Instance of ComicLibComics to use for all tests.
    private val comicLibComics: ComicLibComics

    /**
     * Constructor.
     * Initializes comicLibComics.
     */
    init {
        ContextProvider.setApplicationContext(InstrumentationRegistry.getTargetContext())
        val serverAddress = Preferences.getInstance().getString(Preferences.SERVER_ADDRESS_KEY, "") replaceNull ""
        comicLibComics = ComicLibComics(serverAddress)
    }

    /**
     * Run all tests.
     */
    @Test
    fun test() {
        Logging.logDebug("Starting ComicLibComicsTest")
        testComics()
        Logging.logDebug("Done")
    }

    /**
     * Test requests to the /issues/{id}/file resource.
     */
    private fun testComics(){
        val response = comicLibComics.getComicFile(ISSUE_ID)
        response?.saveFile(ComicsCache.getInstance())
        val fileExists = ComicsCache.getFilesNames()?.contains(response?.filename)?: false
        assert(fileExists)
    }

}