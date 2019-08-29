package de.ahahn94.manhattan.api.responses

import com.google.gson.annotations.SerializedName

/**
 * Data class for token datasets from the ComicLib API.
 */
class Token(
    responseStatus: ResponseStatus?,
    responseContent: Content?
) : ApiResponse<Token.Content?>(responseStatus, responseContent) {

    /**
     * Data class for the content part of token datasets.
     */
    data class Content(
        @SerializedName("APIKey")
        val apiKey: String
    )

}
