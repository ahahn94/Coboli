package de.ahahn94.manhattan.api.responses

import com.google.gson.annotations.SerializedName

/**
 * Data class for token datasets from the ComicLib API.
 */
data class Token(
    @SerializedName("Status")
    val status: ResponseStatus?,
    @SerializedName("Content")
    val content: Content?
) : ApiResponse<Token.Content?>(responseStatus = status, responseContent = content) {

    /**
     * Data class for the content part of token datasets.
     */
    data class Content(
        @SerializedName("APIKey")
        val apiKey: String
    )

}
