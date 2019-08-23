package de.ahahn94.manhattan.api.types

import com.google.gson.annotations.SerializedName

/**
 * Data class for datasets received from the /issues resource.
 */
data class Issue(
    @SerializedName("Status")
    val status: ResponseStatus?,
    @SerializedName("Content")
    val content: Content?
) :
    ApiResponse<Issue.Content?>(responseStatus = status, responseContent = content) {

    /**
     * Data class for the list of issues received from the /issues and /volume{id}/issues resources.
     */
    data class List(
        @SerializedName("Status")
        val status: ResponseStatus?,
        @SerializedName("Content")
        val content: kotlin.collections.List<Content>?) :
        ApiResponse<kotlin.collections.List<Content>?>(responseStatus = status, responseContent = content)

    /**
     * Data class for the content part of Issue.
     */
    data class Content(
        @SerializedName("ID")
        val id: String,
        @SerializedName("Link")
        val link: String,
        @SerializedName("Description")
        val description: String,
        @SerializedName("ImageFileURL")
        val imageFileURL: String,
        @SerializedName("File")
        val file: ComicFile,
        @SerializedName("Number")
        val number: String,
        @SerializedName("Name")
        val name: String,
        @SerializedName("ReadStatus")
        val readStatus: ReadStatus,
        @SerializedName("Volume")
        val volume: Volume
    )

    /**
     * Data class for the comic file part of the Content.
     */
    data class ComicFile(
        @SerializedName("FileName")
        val fileName: String,
        @SerializedName("FileURL")
        val fileURL: String
    )

    /**
     * Data class for the read-status part of the Content.
     */
    data class ReadStatus(
        @SerializedName("IsRead")
        val isRead: Int,
        @SerializedName("CurrentPage")
        val currentPage: Int,
        @SerializedName("Link")
        val link: String
    )

    /**
     * Data class for the volume part of the Content.
     */
    data class Volume(
        @SerializedName("VolumeID")
        val volumeID: String,
        @SerializedName("Link")
        val link: String
    )
}