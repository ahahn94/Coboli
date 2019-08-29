package de.ahahn94.manhattan.api.responses

import de.ahahn94.manhattan.database.Volume

/**
 * Data class for datasets received from the /volumes resource.
 * Wraps the Volume database entity.
 */
class VolumeResponse(
    responseStatus: ResponseStatus,
    responseContent: Volume?
) : ApiResponse<Volume?>(responseStatus, responseContent) {

    /**
     * Data class for the list of volumes received from the /volumes and /publisher/{id}/volumes
     * resource.
     */
    class List(
        responseStatus: ResponseStatus,
        responseContent: kotlin.collections.List<Volume>?
    ) : ApiResponse<kotlin.collections.List<Volume>?>(responseStatus, responseContent)

}