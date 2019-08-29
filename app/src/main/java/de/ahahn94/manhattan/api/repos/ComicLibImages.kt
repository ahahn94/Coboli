package de.ahahn94.manhattan.api.repos

import de.ahahn94.manhattan.api.responses.ApiFile
import de.ahahn94.manhattan.utils.network.TrustedCertificatesClientFactory
import okhttp3.OkHttpClient
import okhttp3.Request

/**
 * Class that handles the image files part of the ComicLib API.
 */
data class ComicLibImages(private val url: String) {

    // OkHttpClient for all calls to this API resource.
    private val client: OkHttpClient = TrustedCertificatesClientFactory.create()

    /**
     * Download the image file specified by the url.
     * Will return an ApiFile containing the files bytes or null if not found/other error.
     */
    fun getImage(imageURL: String): ApiFile? {
        val request = Request.Builder().url(url + imageURL).build()

        val response = client.newCall(request).execute()
        return if (response.isSuccessful) {
            val byteStream = response.body?.byteStream()
            val content = byteStream?.readBytes()
            byteStream?.close()
            val filename = imageURL.split("/").last()

            val image = ApiFile(filename, content)
            image
        } else {
            null
        }
    }

}