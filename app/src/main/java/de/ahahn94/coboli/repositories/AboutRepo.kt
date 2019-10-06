package de.ahahn94.coboli.repositories

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import de.ahahn94.coboli.utils.ContextProvider
import java.nio.charset.Charset

/**
 * Class that gets the information about Coboli and its libraries.
 */
class AboutRepo {

    companion object {

        /**
         * Get the data for the AboutFragment.
         * Consists of information about the app. its libraries
         * and their licenses.
         */
        fun getAbout(): About {
            // Get a map of the licenses.
            val licenses = getLicenses()
            // Get the content of libraries.json as an About.
            val inputStream = ContextProvider.getApplicationContext().assets
                .open("libraries.json")
            val json = String(inputStream.readBytes(), Charset.forName("UTF-8"))
            val about = Gson().fromJson(json, About::class.java)
            // Fill in the licenseTexts.
            about.appInfo.licenseText = licenses[about.appInfo.licenseName] ?: ""
            about.libraries.forEach {
                it.licenseText = licenses[it.licenseName] ?: ""
            }
            about.libraries
            return about
        }

        /**
         * Get the licenses from licenses.json.
         * Returns a map of license name to license text.
         * Many libraries use the same licenses (Apache, GPL, MIT).
         * Storing them in a separate file instead of the libraries file
         * keeps the libraries file much cleaner and also avoids redundancy.
         */
        private fun getLicenses(): Map<String, String> {
            val inputStream = ContextProvider.getApplicationContext().assets
                .open("licenses.json")
            val json = String(inputStream.readBytes(), Charset.forName("UTF-8"))
            val licenses = Gson().fromJson(json, Licenses::class.java)
            return licenses.licenses.map {
                it.name to it.text
            }.toMap()
        }
    }

    /**
     * Information about the app, its libraries and their licenses.
     */
    data class About(
        @SerializedName("App")
        val appInfo: AppInfo,
        @SerializedName("Libraries")
        val libraries: List<Library>
    )

    /**
     * Information about the app and its copyright and license.
     */
    data class AppInfo(
        @SerializedName("Name")
        val name: String,
        @SerializedName("Copyright")
        val copyright: String,
        @SerializedName("LicenseName")
        val licenseName: String,
        @SerializedName("LicenseText")
        var licenseText: String
    )

    /**
     * Information about a library and its license.
     */
    data class Library(
        @SerializedName("Name")
        val name: String,
        @SerializedName("LicenseName")
        val licenseName: String,
        @SerializedName("LicenseText")
        var licenseText: String
    )

    /**
     * Container for the list of licenses from licenses.json.
     */
    private data class Licenses(
        @SerializedName("Licenses")
        val licenses: List<License>
    ) {
        data class License(
            @SerializedName("Name")
            val name: String,
            @SerializedName("Text")
            val text: String
        )
    }

}