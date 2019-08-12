package de.ahahn94.manhattan.utils.security

import java.security.KeyStore

/**
 * Provides a shared KeyStore for storing certificates and cryptographic keys.
 * Used by Cryptography to get an AES key to de-/encrypt user credentials and by
 * KnownServers to store trusted SSL/TLS certificates.
 */
class KeyStoreProvider {

    companion object {

        // Constants.
        internal const val KEY_STORE_TYPE = "AndroidKeyStore"  // Type of the KeyStore.

        // The KeyStore contains the keys for this app.
        internal val keyStore = createKeyStore()

        /**
         * Create and load a KeyStore.
         * Returns the KeyStore identified by KEY_STORE_TYPE.
         */
        private fun createKeyStore(): KeyStore {
            val keyStore = KeyStore.getInstance(KEY_STORE_TYPE)
            keyStore.load(null) // Load without using any password.
            return keyStore
        }

    }

}