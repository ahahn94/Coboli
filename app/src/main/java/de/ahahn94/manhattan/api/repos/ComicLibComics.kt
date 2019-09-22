package de.ahahn94.manhattan.api.repos

import de.ahahn94.manhattan.api.clients.DownloadClientFactory
import de.ahahn94.manhattan.api.responses.ComicFile
import de.ahahn94.manhattan.utils.security.Authentication
import de.ahahn94.manhattan.utils.settings.Credentials
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream

/**
 * Class that handles the comic files part of the ComicLib API.
 */
data class ComicLibComics(private val url: String) {

    companion object {

        // Constants.
        private const val API_COMICS_BASE_PATH = "/api/v1/issues/"
        private const val API_COMICS_FILE_PATH = "/file"

        // Authorization for the API calls.
        val bearerTokenAuthentication =
            Authentication.generateBearerTokenHeader(Credentials.getInstance().apiKey)

    }

    /**
     * Download the comics file specified by issueID.
     */
    fun getComicFile(issueID: String, parent: File): ComicFile? {
        val request = Request.Builder()
            .url(url + API_COMICS_BASE_PATH + issueID + API_COMICS_FILE_PATH)
            .header("Authorization", bearerTokenAuthentication).build()

        val client: OkHttpClient =
            DownloadClientFactory.create(
                DownloadClientFactory.NotifyingProgressListener(
                    issueID.toInt()
                )
            )

        val response = client.newCall(request).execute()
        return if (response.isSuccessful) {

            // Get filename.
            val extension = response.header("Content-Disposition")?.split(".")?.last()
            val filename = "$issueID.$extension"

            // Get input-/outputstreams.
            val file = File(parent, filename)
            val outputStream = FileOutputStream(file, false)    // Overwrite if exists.
            val inputStream = response.body?.byteStream()

            // Download to file.
            inputStream?.copyTo(outputStream)

            // Close streams.
            inputStream?.close()
            outputStream.close()

            ComicFile(file)
        } else {
            null
        }
    }

}