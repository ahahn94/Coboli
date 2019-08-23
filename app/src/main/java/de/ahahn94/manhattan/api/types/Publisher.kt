package de.ahahn94.manhattan.api.types

import com.google.gson.annotations.SerializedName

/**
 * Data class for datasets received from the /publishers resource.
 */
data class Publisher(
    @SerializedName("Status")
    val status: ResponseStatus,
    @SerializedName("Content")
    val content: Content?
) : ApiResponse<Publisher.Content?>(responseStatus = status, responseContent = content) {

    /**
     * Data class for the list of publishers received from the /publishers resource.
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
     * Data class for the content part of Publisher.
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
        @SerializedName("VolumesURL")
        val volumesURL: String,
        @SerializedName("VolumesCount")
        val volumesCount: Int
    )

}