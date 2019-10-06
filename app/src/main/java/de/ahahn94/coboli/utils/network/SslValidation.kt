package de.ahahn94.coboli.utils.network

import android.annotation.SuppressLint
import de.ahahn94.coboli.utils.settings.Preferences
import de.ahahn94.coboli.utils.replaceNull
import java.net.URL
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import javax.net.ssl.*

/**
 * Class to get TLS certificates for validation by the user.
 */
class SslValidation {

    companion object {

        /**
         * Get the TLS certificate from the current server address stored in the Preferences.
         * Will establish a connection to the server to get the certificate.
         * Returns the certificate or null if connection failed.
         */
        fun getCertificate(): X509Certificate? {
            // Get the server address used by the ConnectionTester.
            val address = Preferences.getInstance().getString(
                Preferences.SERVER_ADDRESS_KEY,
                ""
            ) replaceNull "" + ConnectionTester.RESOURCE

            // Turn the address into an URL.
            val url = URL(address)

            // Create an unsafe TrustManager to prevent exceptions if certificate error.
            val trustManager = arrayOf<TrustManager>(object : X509TrustManager {
                override fun getAcceptedIssuers(): Array<X509Certificate>? {
                    return accepted
                }

                var accepted: Array<X509Certificate>? = null

                @SuppressLint("TrustAllX509TrustManager")   // Suppress the warning message about the unsafe TrustManager.
                @Throws(CertificateException::class)
                override fun checkClientTrusted(xcs: Array<X509Certificate>, string: String) {
                }

                @Throws(CertificateException::class)
                override fun checkServerTrusted(xcs: Array<X509Certificate>, string: String) {
                    accepted = xcs
                }
            })

            // Initialize the SSLContext with the unsafe TrustManager.
            val sslContext = SSLContext.getInstance("TLS")
            sslContext.init(null, trustManager, null)

            // Connect to server.
            val connection = url.openConnection() as HttpsURLConnection

            // Set unsafe HostnameVerifier to prevent exceptions if hostname does not match cert.
            connection.hostnameVerifier = HostnameVerifier { _, _ -> true }

            connection.sslSocketFactory = sslContext.socketFactory

            val certificate: X509Certificate?

            if (connection.responseCode == 200) {
                // Successfully connected. Get certificate.
                val certificates = connection.serverCertificates
                certificate = certificates.first() as X509Certificate
            } else certificate = null
            connection.disconnect()
            return certificate
        }

    }

}