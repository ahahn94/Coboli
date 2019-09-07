package de.ahahn94.manhattan.api.responses

import de.ahahn94.manhattan.model.entities.VolumeEntity

/**
 * Data class for datasets received from the /volumes resource.
 * Wraps the Volume database entity.
 */
class VolumeResponse(
    responseStatus: ResponseStatus,
    responseContent: VolumeEntity?
) : ApiResponse<VolumeEntity?>(responseStatus, responseContent) {

    /**
     * Data class for the list of volumes received from the /volumes and /publisher/{id}/volumes
     * resource.
     */
    class List(
        responseStatus: ResponseStatus,
        responseContent: kotlin.collections.List<VolumeEntity>?
    ) : ApiResponse<kotlin.collections.List<VolumeEntity>?>(responseStatus, responseContent)

}