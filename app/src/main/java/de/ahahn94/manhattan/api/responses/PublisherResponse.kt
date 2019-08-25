package de.ahahn94.manhattan.api.responses

import com.google.gson.annotations.SerializedName
import de.ahahn94.manhattan.database.Publisher

/**
 * Data class for datasets received from the /publishers resource.
 * Wraps the Publisher database entity.
 */
data class PublisherResponse(
    @SerializedName("Status")
    val status: ResponseStatus,
    @SerializedName("Content")
    val content: Publisher?
) : ApiResponse<Publisher?>(responseStatus = status, responseContent = content) {

    /**
     * Data class for the list of publishers received from the /publishers resource.
     */
    data class List(
        @SerializedName("Status")
        val status: ResponseStatus,
        @SerializedName("Content")
        val content: kotlin.collections.List<Publisher>?
    ) : ApiResponse<kotlin.collections.List<Publisher>?>(
        responseStatus = status,
        responseContent = content
    )
    
}