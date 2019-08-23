package de.ahahn94.manhattan


import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import de.ahahn94.manhattan.api.repos.ComicLibImages
import de.ahahn94.manhattan.cache.ImagesCache
import de.ahahn94.manhattan.utils.ContextProvider
import de.ahahn94.manhattan.utils.Logging
import de.ahahn94.manhattan.utils.replaceNull
import de.ahahn94.manhattan.utils.settings.Preferences
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Unit tests for the functions that handle the image file parts of the ComicLib API.
 */
@RunWith(AndroidJUnit4::class)
class ComicLibImagesTest {

    companion object {

        // Constants for testing. Filename taken from the example datasets of ComicLib..
        private const val IMAGE_URL = "4000-511011.jpg"

    }

    // Instance of ComicLibImages to use for all tests.
    private val comicLibImages: ComicLibImages

    /**
     * Constructor.
     * Initializes comicLibImages.
     */
    init {
        ContextProvider.setApplicationContext(InstrumentationRegistry.getTargetContext())
        val serverAddress =
            Preferences.getInstance().getString(Preferences.SERVER_ADDRESS_KEY, "") replaceNull ""
        comicLibImages = ComicLibImages(serverAddress)
    }

    /**
     * Run all tests.
     */
    @Test
    fun test() {
        Logging.logDebug("Starting ComicLibImagesTest")
        testImages()
        Logging.logDebug("Done")
    }

    /**
     * Test requests to the /cache/images/ resource.
     */
    private fun testImages() {
        val response = comicLibImages.getImage(IMAGE_URL)
        response?.saveFile(ImagesCache.getInstance())
        val fileExists = ImagesCache.getFilesNames()?.contains(response?.filename) ?: false
        assert(fileExists)
    }

}