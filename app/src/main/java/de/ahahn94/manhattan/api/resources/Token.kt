package de.ahahn94.manhattan.api.resources

/**
 * Data class for token datasets from the ComicLib API.
 */
data class Token (val Status : ResponseStatus, val Content : TokenContent) {

    /**
     * Data class for the content part of token datasets.
     */
    data class TokenContent (val APIKey : String)

}
