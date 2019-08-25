package de.ahahn94.manhattan


import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.gson.Gson
import de.ahahn94.manhattan.api.repos.ComicLibAPI
import de.ahahn94.manhattan.api.responses.ApiResponse
import de.ahahn94.manhattan.api.responses.IssueReadStatus
import de.ahahn94.manhattan.api.responses.Token
import de.ahahn94.manhattan.api.responses.VolumeReadStatus
import de.ahahn94.manhattan.database.Issue
import de.ahahn94.manhattan.database.Publisher
import de.ahahn94.manhattan.database.Volume
import de.ahahn94.manhattan.utils.ContextProvider
import de.ahahn94.manhattan.utils.Logging
import de.ahahn94.manhattan.utils.replaceNull
import de.ahahn94.manhattan.utils.settings.Preferences
import org.junit.Test
import org.junit.runner.RunWith
import retrofit2.Response

/**
 * Unit tests for the functions that handle the JSON-based parts of the ComicLib API.
 */
@RunWith(AndroidJUnit4::class)
class ComicLibApiTest {

    companion object {

        // Constants for testing. IDs taken from the example datasets of ComicLib.
        private const val ISSUE_ID = "511011"
        private const val VOLUME_ID = "84961"
        private const val PUBLISHER_ID = "4212"

    }

    // Instance of ComicLibAPI to use for all tests.
    private val comicLibAPI: ComicLibAPI

    /**
     * Constructor.
     * Initialize comicLibAPI.
     */
    init {
        ContextProvider.setApplicationContext(InstrumentationRegistry.getInstrumentation().targetContext)
        val serverAddress =
            Preferences.getInstance().getString(Preferences.SERVER_ADDRESS_KEY, "") replaceNull ""
        comicLibAPI = ComicLibAPI(serverAddress)
    }

    /**
     * Run all tests.
     */
    @Test
    fun test() {
        Logging.logDebug("Starting ComicLibApiTest")
        testTokens()
        testIssues()
        testIssue()
        testIssueReadStatus()
        testVolumes()
        testVolumeIssues()
        testVolumeReadStatus()
        testPublishers()
        testPublisher()
        testPublisherVolumes()
        Logging.logDebug("Done")
    }

    /**
     * Test requests to the /tokens resource.
     */
    private fun testTokens() {
        val response = comicLibAPI.getToken()
        val obj = getObjectFromResponse(response)
        testObject(obj as ApiResponse<Token.Content?>)

    }

    /**
     * Test requests to the /issues resource.
     */
    private fun testIssues() {
        val response = comicLibAPI.getIssues()
        val obj = getObjectFromResponse(response)
        testObject(obj as ApiResponse<List<Issue>?>)
    }

    /**
     * Test requests to the /issues/{id} resource.
     */
    private fun testIssue() {
        val response = comicLibAPI.getIssue(ISSUE_ID)
        val obj = getObjectFromResponse(response)
        testObject(obj as ApiResponse<Issue?>)
    }

    /**
     * Test requests to the /issues/{id}/readstatus resource.
     */
    private fun testIssueReadStatus() {
        val response = comicLibAPI.getIssueReadStatus(ISSUE_ID)
        val obj = getObjectFromResponse(response)
        testObject(obj as ApiResponse<IssueReadStatus.Content?>)

        // Test PUT
        obj.content?.isRead = IssueReadStatus.IS_READ_READ
        obj.content?.currentPage = IssueReadStatus.CURRENT_PAGE_NO_PROGRESS
        val response2 = comicLibAPI.putIssueReadStatus(ISSUE_ID, obj.content!!)
        val obj2 = getObjectFromResponse(response2)
        testObject(obj2 as ApiResponse<IssueReadStatus.Content?>)
    }

    /**
     * Test requests to the /volumes resource.
     */
    private fun testVolumes() {
        val response = comicLibAPI.getVolumes()
        val obj = getObjectFromResponse(response)
        testObject(obj as ApiResponse<List<Volume>?>)
    }

    /**
     * Test requests to the /volumes/{id} resource.
     */
    fun testVolume() {
        val response = comicLibAPI.getVolume(VOLUME_ID)
        val obj = getObjectFromResponse(response)
        testObject(obj as ApiResponse<Volume?>)
    }

    /**
     * Test requests to the /volumes/{id}/issues resource.
     */
    private fun testVolumeIssues() {
        val response = comicLibAPI.getVolumeIssues(VOLUME_ID)
        val obj = getObjectFromResponse(response)
        testObject(obj as ApiResponse<List<Issue>?>)
    }

    /**
     * Test requests to the /volumes/{id}/readstatus resource.
     */
    private fun testVolumeReadStatus() {
        val response = comicLibAPI.getVolumeReadStatus(VOLUME_ID)
        val obj = getObjectFromResponse(response)
        testObject(obj as ApiResponse<VolumeReadStatus.Content?>)

        // Test PUT
        obj.content?.isRead = VolumeReadStatus.IS_READ_READ
        val response2 = comicLibAPI.putVolumeReadStatus(VOLUME_ID, obj.content!!)
        val obj2 = getObjectFromResponse(response2)
        testObject(obj2 as ApiResponse<VolumeReadStatus.Content?>)
    }

    /**
     * Test requests to the /publishers resource.
     */
    private fun testPublishers() {
        val response = comicLibAPI.getPublishers()
        val obj = getObjectFromResponse(response)
        testObject(obj as ApiResponse<List<Publisher>?>)
    }

    /**
     * Test requests to the /publishers/{id} resource.
     */
    private fun testPublisher() {
        val response = comicLibAPI.getPublisher(PUBLISHER_ID)
        val obj = getObjectFromResponse(response)
        testObject(obj as ApiResponse<Publisher?>)
    }

    /**
     * Test requests to the /publishers/{id}/volumes resource.
     */
    private fun testPublisherVolumes() {
        val response = comicLibAPI.getPublisherVolumes(PUBLISHER_ID)
        val obj = getObjectFromResponse(response)
        testObject(obj as ApiResponse<List<Volume>?>)
    }

    /**
     * Get the body from a Response.
     * Manually parse the errorBody of the response, as the body will be empty if response code != 200.
     */
    private inline fun <reified T> getObjectFromResponse(response: Response<T?>): T? {
        return if (response.isSuccessful) {
            response.body()
        } else {
            val responseBody = response.errorBody()?.string() ?: ""
            val gson = Gson()
            val obj = gson.fromJson<T>(responseBody, T::class.java)
            obj
        }
    }

    /**
     * Test an ApiResponse.
     * Tests if obj.status and obj.content are not null.
     */
    private fun <T> testObject(obj: ApiResponse<T?>) {
        val status = obj.responseStatus
        val content = obj.responseContent
        assert(status != null)
        assert(content != null)
    }

}