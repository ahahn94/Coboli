package de.ahahn94.manhattan.api.responses

import com.google.gson.annotations.SerializedName
import de.ahahn94.manhattan.database.Issue

/**
 * Data class for datasets received from the /issues resource.
 * Wraps the Issue database entity.
 */
data class IssueResponse(
    @SerializedName("Status")
    val status: ResponseStatus?,
    @SerializedName("Content")
    val content: de.ahahn94.manhattan.database.Issue?
) :
    ApiResponse<Issue?>(responseStatus = status, responseContent = content) {

    /**
     * Data class for the list of issues received from the /issues and /volume{id}/issues resources.
     */
    data class List(
        @SerializedName("Status")
        val status: ResponseStatus?,
        @SerializedName("Content")
        val content: kotlin.collections.List<Issue>?
    ) :
        ApiResponse<kotlin.collections.List<Issue>?>(
            responseStatus = status,
            responseContent = content
        )

}