package de.ahahn94.coboli.api.responses

import de.ahahn94.coboli.model.entities.IssueEntity

/**
 * Data class for datasets received from the /issues resource.
 * Wraps the Issue database entity.
 */
class IssueResponse(
    responseStatus: ResponseStatus?,
    responseContent: IssueEntity?
) :
    ApiResponse<IssueEntity?>(responseStatus, responseContent) {

    /**
     * Data class for the list of issues received from the /issues and /volume{id}/issues resources.
     */
    class List(
        responseStatus: ResponseStatus?,
        responseContent: kotlin.collections.List<IssueEntity>?
    ) :
        ApiResponse<kotlin.collections.List<IssueEntity>?>(responseStatus, responseContent)

}