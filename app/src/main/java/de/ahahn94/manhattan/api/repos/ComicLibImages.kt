package de.ahahn94.manhattan.api.repos

import de.ahahn94.manhattan.api.responses.ApiFile
import de.ahahn94.manhattan.utils.network.TrustedCertificatesClientFactory
import okhttp3.OkHttpClient
import okhttp3.Request

/**
 * Class that handles the image files part of the ComicLib API.
 */
data class ComicLibImages(private val url: String) {

    companion object {

        // Constants.
        private const val API_IMAGES_BASE_PATH = "/cache/images/"

    }

    // OkHttpClient for all calls to this API resource.
    private val client: OkHttpClient = TrustedCertificatesClientFactory.create()

    /**
     * Download the image file specified by the filename.
     * Will return an ApiFile containing the files bytes or null if not found/other error.
     */
    fun getImage(filename: String): ApiFile? {
        val request = Request.Builder().url(url + API_IMAGES_BASE_PATH + filename).build()

        val response = client.newCall(request).execute()
        return if (response.isSuccessful) {
            val byteStream = response.body?.byteStream()
            val content = byteStream?.readBytes()
            byteStream?.close()

            val image = ApiFile(filename, content)
            image
        } else {
            null
        }
    }

}