package de.ahahn94.manhattan.api.types

/**
 * Interface for ComicLib API responses.
 * Valid responses will always contain a JSON-encoded status part and a JSON-encoded response part
 * which can be mapped to a resource-specific class.
 */
open class ApiResponse<T>(open val responseStatus: ResponseStatus?, open val responseContent: T)