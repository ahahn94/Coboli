package de.ahahn94.manhattan.api.types

import com.google.gson.annotations.SerializedName

/**
 * Data class for datasets received from the /issues/{id}/readstatus resource.
 */
data class IssueReadStatus(
    @SerializedName("Status")
    val status: ResponseStatus,
    @SerializedName("Content")
    val content: Content?
) :
    ApiResponse<IssueReadStatus.Content?>(status, content) {

    companion object {

        // Constants.
        const val IS_READ_READ = "1"
        const val IS_READ_UNREAD = "0"
        const val CURRENT_PAGE_READ = "0"

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
        var currentPage: String
    )

}