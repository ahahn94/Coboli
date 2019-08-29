package de.ahahn94.manhattan.api.responses

import de.ahahn94.manhattan.database.Publisher

/**
 * Data class for datasets received from the /publishers resource.
 * Wraps the Publisher database entity.
 */
class PublisherResponse(
    responseStatus: ResponseStatus,
    responseContent: Publisher?
) : ApiResponse<Publisher?>(responseStatus, responseContent) {

    /**
     * Data class for the list of publishers received from the /publishers resource.
     */
    class List(
        responseStatus: ResponseStatus,
        responseContent: kotlin.collections.List<Publisher>?
    ) : ApiResponse<kotlin.collections.List<Publisher>?>(responseStatus, responseContent)

}