package de.ahahn94.manhattan.api.responses

import com.google.gson.annotations.SerializedName
import de.ahahn94.manhattan.database.Volume

/**
 * Data class for datasets received from the /volumes resource.
 * Wraps the Volume database entity.
 */
data class VolumeResponse(
    @SerializedName("Status")
    val status: ResponseStatus,
    @SerializedName("Content")
    val content: Volume?
) : ApiResponse<Volume?>(responseStatus = status, responseContent = content) {

    /**
     * Data class for the list of volumes received from the /volumes and /publisher/{id}/volumes
     * resource.
     */
    data class List(
        @SerializedName("Status")
        val status: ResponseStatus,
        @SerializedName("Content")
        val content: kotlin.collections.List<Volume>?
    ) : ApiResponse<kotlin.collections.List<Volume>?>(
        responseStatus = status,
        responseContent = content
    )

}