package de.ahahn94.manhattan.api.types

import com.google.gson.annotations.SerializedName

/**
 * Data class for the status-part of ComicLib API responses.
 */
data class ResponseStatus(
    @SerializedName("ResponseCode")
    val responseCode: Int,
    @SerializedName("ResponseMessage")
    val responseMessage: String)