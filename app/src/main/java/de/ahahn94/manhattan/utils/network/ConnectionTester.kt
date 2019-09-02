package de.ahahn94.manhattan.utils.network

import de.ahahn94.manhattan.R
import de.ahahn94.manhattan.api.repos.ComicLibAPI
import de.ahahn94.manhattan.utils.ContextProvider
import de.ahahn94.manhattan.utils.Localization
import de.ahahn94.manhattan.utils.network.ConnectionStatusType.OK
import de.ahahn94.manhattan.utils.network.ConnectionStatusType.PARAM_ERROR
import de.ahahn94.manhattan.utils.replaceNull
import de.ahahn94.manhattan.utils.settings.Preferences
import java.net.ConnectException
import java.net.UnknownHostException
import java.security.cert.CertPathValidatorException
import java.security.cert.CertificateException
import java.security.cert.CertificateExpiredException
import java.security.cert.CertificateNotYetValidException
import javax.net.ssl.SSLHandshakeException
import javax.net.ssl.SSLPeerUnverifiedException

/**
 * Class that checks the connection to the ComicLib server.
 */
class ConnectionTester {

    companion object {

        // Constants.
        const val RESOURCE = "/api/v1/tokens" // API resource to use for testing.

        /**
         * Test the connection to the server address from Preferences.
         * Return a ConnectionStatus.
         * The function trys to get a bearer token from the ComicLib API using the server address
         * from Preferences and the username and password from Credentials.
         * The ConnectionStatus differentiates between successful connection, parameter errors (which result
         * in http error codes) and ssl errors (which result in exceptions).
         */
        fun test(): ConnectionStatus {

            val serverAddress =
                Preferences.getInstance().getString(Preferences.SERVER_ADDRESS_KEY, "") replaceNull ""
            if (serverAddress != "") {
                var status: ConnectionStatus
                try {
                    // Try to get an response from the server. Possible point of failure.
                    val response =
                        ComicLibAPI(
                            Preferences.getInstance().getString(
                                Preferences.SERVER_ADDRESS_KEY,
                                ""
                            ) replaceNull ""
                        ).getToken()

                    // No exception while testing. Prepare connection status.
                    val message: String
                    val statusType: ConnectionStatusType

                    // Get message and code based on http code of the response.
                    when (response.code()) {
                        200 -> {
                            // Connection ok.
                            message = Localization.getLocalizedString(R.string.login_successful)
                            statusType = OK
                        }
                        400 -> {
                            // Probably an error in the server address.
                            message = Localization.getLocalizedString(R.string.bad_request)
                            statusType = PARAM_ERROR
                        }
                        401 -> {
                            // Wrong credentials.
                            message = Localization.getLocalizedString(R.string.login_failed)
                            statusType = PARAM_ERROR
                        }
                        404 -> {
                            // Wrong server address or server is down.
                            message = Localization.getLocalizedString(R.string.server_not_found)
                            statusType = PARAM_ERROR
                        }
                        else -> {
                            // Other, less common errors.
                            // Set error code and response text (html) as message.
                            message = response.errorBody()?.string() ?: ""
                            statusType = PARAM_ERROR
                        }
                    }
                    status =
                        ConnectionStatus(response.code(), message, statusType, response)
                } catch (e: Exception) {
                    // Error while testing. Prepare connection status.
                    val message = e.message.toString()
                    val code: Int
                    val statusType: ConnectionStatusType

                    // Get code and status type based on the exception type.
                    // Exceptions with status type SSL_ERROR are fixable via adding the certificate to the known servers.
                    // Exceptions with status type PARAM_ERROR are probably caused by a bad server address.
                    when (e) {
                        is CertificateExpiredException -> {
                            code = 526  // Using 526 (Invalid SSL Certificate), which is an unofficial HTTP code.
                            statusType = ConnectionStatusType.SSL_ERROR
                        }
                        is CertificateNotYetValidException -> {
                            code = 526  // Using 526 (Invalid SSL Certificate), which is an unofficial HTTP code.
                            statusType = ConnectionStatusType.SSL_ERROR
                        }
                        is CertPathValidatorException -> {
                            code = 526  // Using 526 (Invalid SSL Certificate), which is an unofficial HTTP code.
                            statusType = ConnectionStatusType.SSL_ERROR
                        }
                        is CertificateException -> {
                            code = 526  // Using 526 (Invalid SSL Certificate), which is an unofficial HTTP code.
                            statusType = ConnectionStatusType.SSL_ERROR
                        }
                        is SSLPeerUnverifiedException -> {
                            code = 526  // Using 526 (Invalid SSL Certificate), which is an unofficial HTTP code.
                            statusType = ConnectionStatusType.SSL_ERROR
                        }
                        is SSLHandshakeException -> {
                            if (e.cause?.cause is CertPathValidatorException) {
                                code = 526  // Using 526 (Invalid SSL Certificate), which is an unofficial HTTP code.
                                statusType = ConnectionStatusType.SSL_ERROR
                            } else {
                                code = 400  // Handshake failed -> Bad request.
                                statusType = PARAM_ERROR
                            }
                        }
                        is ConnectException -> {
                            code = 404
                            statusType = PARAM_ERROR
                        }
                        is UnknownHostException -> {
                            code = 404
                            statusType = PARAM_ERROR
                        }
                        else -> throw  e
                    }

                    status = ConnectionStatus(code, message, statusType, null, e)
                }

                return status
            } else {
                // Empty server address.
                return ConnectionStatus(
                    404,
                    Localization.getLocalizedString(R.string.server_not_found),
                    PARAM_ERROR
                )
            }
        }

    }

}