package de.ahahn94.manhattan.api.responses

import com.google.gson.annotations.SerializedName

/**
 * Data class for datasets received from the /volumes/{id}/readstatus resource.
 */
data class VolumeReadStatus(
    @SerializedName("Status")
    val status: ResponseStatus,
    @SerializedName("Content")
    val content: Content?
) :
    ApiResponse<VolumeReadStatus.Content?>(status, content) {

    companion object {

        // Constants.
        const val IS_READ_READ = "1"
        const val IS_READ_UNREAD = "0"

    }

    /**
     * Data class for the content part of VolumeReadStatus.
     */
    data class Content(
        @SerializedName("VolumeID")
        val volumeID: String,
        @SerializedName("IsRead")
        var isRead: String
    )

}