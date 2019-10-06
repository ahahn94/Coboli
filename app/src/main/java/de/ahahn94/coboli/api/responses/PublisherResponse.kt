package de.ahahn94.coboli.api.responses

import de.ahahn94.coboli.model.entities.PublisherEntity

/**
 * Data class for datasets received from the /publishers resource.
 * Wraps the Publisher database entity.
 */
class PublisherResponse(
    responseStatus: ResponseStatus,
    responseContent: PublisherEntity?
) : ApiResponse<PublisherEntity?>(responseStatus, responseContent) {

    /**
     * Data class for the list of publishers received from the /publishers resource.
     */
    class List(
        responseStatus: ResponseStatus,
        responseContent: kotlin.collections.List<PublisherEntity>?
    ) : ApiResponse<kotlin.collections.List<PublisherEntity>?>(responseStatus, responseContent)

}