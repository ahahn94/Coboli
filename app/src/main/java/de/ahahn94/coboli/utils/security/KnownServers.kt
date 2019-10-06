package de.ahahn94.coboli.utils.security

import de.ahahn94.coboli.utils.replaceNull
import de.ahahn94.coboli.utils.settings.Preferences
import java.net.URL
import java.security.cert.Certificate

/**
 * Class that handles the known/trusted servers.
 */
class KnownServers {

    companion object {

        // Constants.
        private const val CERTIFICATE_KEY = "CoboliTrustedCertificate"
        private const val SERVER_NAME_KEY = "CoboliTrustedServerName"

        // KeyStore to store the trusted server certificates.
        private val keyStore = KeyStoreProvider.keyStore

        /**
         * Add a certificate to the KeyStore of the trusted certificates.
         * Will also safe the hostname of the now trusted server.
         */
        internal fun saveCertificate(certificate: Certificate) {
            keyStore.setCertificateEntry(CERTIFICATE_KEY, certificate)
            val serverAddress = Preferences.getInstance().getString(Preferences.SERVER_ADDRESS_KEY, "") replaceNull ""
            val hostname = URL(serverAddress).host
            Preferences.putString(SERVER_NAME_KEY, hostname)
        }

        /**
         * Get the certificate of the trusted server.
         * Returns a Certificate that is probably a X509Certificate.
         */
        internal fun getCertificate(): Certificate? {
            return keyStore.getCertificate(CERTIFICATE_KEY)
        }

        /**
         * Get the hostname of the trusted server.
         */
        internal fun getServerName(): String {
            return Preferences.getInstance().getString(SERVER_NAME_KEY, "") replaceNull ""
        }

    }

}