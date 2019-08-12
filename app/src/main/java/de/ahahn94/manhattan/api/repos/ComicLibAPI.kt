package de.ahahn94.manhattan.api.repos

import de.ahahn94.manhattan.api.resources.Token
import de.ahahn94.manhattan.utils.security.Authentication
import de.ahahn94.manhattan.utils.security.KnownServers
import de.ahahn94.manhattan.utils.settings.Credentials
import okhttp3.OkHttpClient
import okhttp3.internal.tls.OkHostnameVerifier
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import java.security.KeyStore
import javax.net.ssl.*

/**
 * Class that handles access to the ComicLib API resources.
 */
class ComicLibAPI(url: String) {

    companion object {

        // Constants.
        private const val API_V1_BASE_PATH = "/api/v1/"

    }

    // Instance of the API.
    private var instance: ComicLibApiInterface

    // Authentication strings for the authorization headers.
    private var basicAuthentication: String
    private var bearerTokenAuthentication: String

    // Init-block of the default constructor. Init instance and authentication strings.
    init {
        instance = Retrofit.Builder()
            .baseUrl(url + API_V1_BASE_PATH)
            .addConverterFactory(GsonConverterFactory.create())

            .client(getTrustedCertificatesClient())

            .build().create(ComicLibApiInterface::class.java)
        val credentials = Credentials.getInstance()
        basicAuthentication = Authentication.generateBasicAuthHeader(credentials.username, credentials.password)
        bearerTokenAuthentication = Authentication.generateBearerTokenHeader(credentials.apiKey)
    }

    /**
     * Get a custom OkHttpClient that accepts the certificate and hostname of the known server.
     * This will enable HTTPS-connections with servers even when there is a certificate error.
     * Returns an OkHttpClient preloaded with the certificate and hostname of the known server.
     */
    private fun getTrustedCertificatesClient(): OkHttpClient {
        val builder = OkHttpClient().newBuilder()

        // Get trusted certificate.
        val cert = KnownServers.getCertificate()

        if (cert != null) {
            // Prepare TrustManager preloaded with known server certificate.
            val keyStore = KeyStore.getInstance(KeyStore.getDefaultType())
            keyStore.load(null)
            keyStore.setCertificateEntry(cert.hashCode().toString(), cert)
            val keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm())
            keyManagerFactory.init(keyStore, null)
            val trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
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
        return builder.build()
    }

    /**
     * Get the bearer token of the logged-in user from the /tokens resource.
     * Returns a Token.
     */
    fun getToken(): Response<Token?> {
        return instance.getToken(basicAuthentication).execute()
    }

    /**
     * Retrofit-interface for the ComicLib API.
     */
    interface ComicLibApiInterface {
        @GET("tokens")
        fun getToken(@Header("Authorization") authorization: String): Call<Token?>
    }

}
