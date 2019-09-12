package de.ahahn94.manhattan.api.clients

import de.ahahn94.manhattan.utils.security.KnownServers
import okhttp3.OkHttpClient
import okhttp3.internal.tls.OkHostnameVerifier
import java.security.KeyStore
import javax.net.ssl.*

/**
 * Factory class that produces OkHttpClients with a TrustManager that is preloaded with
 * the certificate of the known server.
 */
class TrustedCertificatesClientFactory {

    companion object {

        /**
         * Get a custom OkHttpClient that accepts the certificate and hostname of the known server.
         * This will enable HTTPS-connections with servers even when there is a certificate error.
         * Returns an OkHttpClient preloaded with the certificate and hostname of the known server.
         */
        fun create(): OkHttpClient {
            val builder =
                createPreconfiguredBuilder()
            return builder.build()
        }

        /**
         * Create an OkHttpClient.Builder that is preloaded with
         * a TrustManager and SslSocketFactory that accept the
         * hostname and certificate of the known server.
         */
        fun createPreconfiguredBuilder(): OkHttpClient.Builder {
            val builder = OkHttpClient().newBuilder()

            // Get trusted certificate.
            val cert = KnownServers.getCertificate()

            if (cert != null) {
                // Prepare TrustManager preloaded with known server certificate.
                val keyStore = KeyStore.getInstance(KeyStore.getDefaultType())
                keyStore.load(null)
                keyStore.setCertificateEntry(cert.hashCode().toString(), cert)
                val keyManagerFactory =
                    KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm())
                keyManagerFactory.init(keyStore, null)
                val trustManagerFactory =
                    TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
                trustManagerFactory.init(keyStore)
                val trustManager = trustManagerFactory.trustManagers[0] as X509TrustManager
                val sslContext = SSLContext.getInstance("TLS")
                sslContext.init(null, arrayOf(trustManager), null)
                val sslSocketFactory = sslContext.socketFactory
                builder.sslSocketFactory(sslSocketFactory, trustManager)

                // Preload HostnameVerifier with the hostname of the known server.
                val hostnameVerifier = object : HostnameVerifier {
                    override fun verify(hostname: String?, session: SSLSession?): Boolean {
                        val trustedHostname = KnownServers.getServerName()
                        if (hostname.equals(trustedHostname)) return true
                        return OkHostnameVerifier.verify(hostname!!, session!!)
                    }
                }

                builder.hostnameVerifier(hostnameVerifier)
            }
            return builder
        }

    }

}