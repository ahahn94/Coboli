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

    companion object {

        // Constants.
        const val IS_READ_READ = "1"
        const val IS_READ_UNREAD = "0"
        const val CURRENT_PAGE_NO_PROGRESS = "0"

    }

    /**
     * Data class for the content part of IssueReadStatus.
     */
    data class Content(
        @SerializedName("IssueID")
        val issueID: String,
        @SerializedName("IsRead")
        var isRead: String,
        @SerializedName("CurrentPage")
        var currentPage: String,
        @SerializedName("Changed")
        var timestampChanged : String
    )

}