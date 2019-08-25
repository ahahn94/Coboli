package de.ahahn94.manhattan.api.repos

import de.ahahn94.manhattan.api.responses.ApiFile
import de.ahahn94.manhattan.utils.network.TrustedCertificatesClientFactory
import de.ahahn94.manhattan.utils.security.Authentication
import de.ahahn94.manhattan.utils.settings.Credentials
import okhttp3.OkHttpClient
import okhttp3.Request

/**
 * Class that handles the comic files part of the ComicLib API.
 */
data class ComicLibComics(private val url: String) {

    companion object {

        // Constants.
        private const val API_COMICS_BASE_PATH = "/api/v1/issues/"
        private const val API_COMICS_FILE_PATH = "/file"

        // Authorization for the API calls.
        val bearerTokenAuthentication = Authentication.generateBearerTokenHeader(Credentials.getInstance().apiKey)

    }

    // OkHttpClient for all calls to this API resource.
    private val client: OkHttpClient = TrustedCertificatesClientFactory.create()

    /**
     * Download the comics file specified by issueID.
     * Will return an ApiFile containing the files bytes or null if not found/other error.
     */
    fun getComicFile(issueID: String): ApiFile? {
        val request = Request.Builder()
            .url(url + API_COMICS_BASE_PATH + issueID + API_COMICS_FILE_PATH)
            .header("Authorization", bearerTokenAuthentication).build()

        val response = client.newCall(request).execute()
        return if (response.isSuccessful) {
            val byteStream = response.body?.byteStream()
            val content = byteStream?.readBytes()
            byteStream?.close()

            val extension = response.header("Content-Disposition")?.split(".")?.last()
            val filename = "$issueID.$extension"

            val comicFile = ApiFile(filename, content)
            comicFile
        } else {
            null
        }
    }

}