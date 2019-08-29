package de.ahahn94.manhattan.api.responses

import com.google.gson.annotations.SerializedName

/**
 * Interface for ComicLib API responses.
 * Valid responses will always contain a JSON-encoded status part and a JSON-encoded response part
 * which can be mapped to a resource-specific class.
 */
open class ApiResponse<T>(
    @SerializedName("Status")
    open val responseStatus: ResponseStatus?,
    @SerializedName("Content")
    open val responseContent: T
)