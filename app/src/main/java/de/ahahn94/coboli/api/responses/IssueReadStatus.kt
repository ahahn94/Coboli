package de.ahahn94.coboli.api.responses

import com.google.gson.annotations.SerializedName

/**
 * Data class for datasets received from the /issues/{id}/readstatus resource.
 */
class IssueReadStatus(
    responseStatus: ResponseStatus,
    responseContent: Content?
) :
    ApiResponse<IssueReadStatus.Content?>(responseStatus, responseContent) {

    /**
     * Data class for the content part of IssueReadStatus.
     */
    data class Content(
        @SerializedName("IssueID")
        val issueID: String,
        @SerializedName("IsRead")
        var isRead: Boolean,
        @SerializedName("CurrentPage")
        var currentPage: Int,
        @SerializedName("Changed")
        var timestampChanged : String
    )

}