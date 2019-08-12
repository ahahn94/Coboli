package de.ahahn94.manhattan.utils.network

import de.ahahn94.manhattan.api.resources.Token
import retrofit2.Response

/**
 * Data class for connection status details.
 */
data class ConnectionStatus(
    val code: Int,
    val message: String,
    val statusType: ConnectionStatusType,
    val response: Response<Token?>? = null,
    val exception: Exception? = null
)

/**
 * Enum for the connection status types:
 * OK: HTTP code 200. Connection and response ok.
 * PARAM_ERROR: HTTP code other than 200. Error in server address, credentials or connection.
 * SSL_ERROR: Problems with the validation of the TLS certificate.
 */
enum class ConnectionStatusType {
    OK, PARAM_ERROR, SSL_ERROR
}