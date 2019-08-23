package de.ahahn94.manhattan.api.types

import com.google.gson.annotations.SerializedName

/**
 * Data class for datasets received from the /volumes resource.
 */
data class Volume(
    @SerializedName("Status")
    val status: ResponseStatus,
    @SerializedName("Content")
    val content: Content?
) : ApiResponse<Volume.Content?>(responseStatus = status, responseContent = content) {

    /**
     * Data class for the list of volumes received from the /volumes and /publisher/{id}/volumes
     * resource.
     */
    data class List(
        @SerializedName("Status")
        val status: ResponseStatus,
        @SerializedName("Content")
        val content: kotlin.collections.List<Content>?
    ) : ApiResponse<kotlin.collections.List<Content>?>(
        responseStatus = status,
        responseContent = content
    )

    /**
     * Data class for the content part of Volume.
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
        @SerializedName("Name")
        val name: String,
        @SerializedName("StartYear")
        val startYear: String,
        @SerializedName("IssuesURL")
        val issuesURL: String,
        @SerializedName("IssuesCount")
        val issuesCount: Int,
        @SerializedName("ReadStatus")
        val readStatus: ReadStatus,
        @SerializedName("Publisher")
        val publisher: Publisher
    )

    /**
     * Data class for the read-status part of Content.
     */
    data class ReadStatus(
        @SerializedName("IsRead")
        val isRead: Int,
        @SerializedName("Link")
        val link: String
    )

    /**
     * Data class for the publisher part of Content.
     */
    data class Publisher(
        @SerializedName("PublisherID")
        val publisherID: String,
        @SerializedName("Link")
        val link: String
    )

}