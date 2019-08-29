package de.ahahn94.manhattan.api.responses

import de.ahahn94.manhattan.database.Issue

/**
 * Data class for datasets received from the /issues resource.
 * Wraps the Issue database entity.
 */
class IssueResponse(
    responseStatus: ResponseStatus?,
    responseContent: de.ahahn94.manhattan.database.Issue?
) :
    ApiResponse<Issue?>(responseStatus, responseContent) {

    /**
     * Data class for the list of issues received from the /issues and /volume{id}/issues resources.
     */
    class List(
        responseStatus: ResponseStatus?,
        responseContent: kotlin.collections.List<Issue>?
    ) :
        ApiResponse<kotlin.collections.List<Issue>?>(responseStatus, responseContent)

}