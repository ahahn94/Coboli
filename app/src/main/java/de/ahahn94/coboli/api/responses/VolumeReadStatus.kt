package de.ahahn94.coboli.api.responses

import com.google.gson.annotations.SerializedName

/**
 * Data class for datasets received from the /volumes/{id}/readstatus resource.
 */
class VolumeReadStatus(
    responseStatus: ResponseStatus,
    responseContent: Content?
) :
    ApiResponse<VolumeReadStatus.Content?>(responseStatus, responseContent) {

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
        var isRead: Boolean,
        @SerializedName("Changed")
        var timestampChanged: String
    )

}