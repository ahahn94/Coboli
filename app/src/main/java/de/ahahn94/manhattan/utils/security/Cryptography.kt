package de.ahahn94.manhattan.utils.security

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import android.util.Log
import java.nio.charset.Charset
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

/**
 * Implements a singleton class for cryptographic functions.
 */
class Cryptography {

    // Singleton object.
    companion object {

        // Constants
        private const val KEY_ALIAS = "Manhattan"             // Alias of the AES key to generate/load.
        private const val AES_MODE = "AES/GCM/NoPadding"      // Options for AES encryption/decryption.
        private const val IV_LENGTH = 12                      // Length of the initial value of the SecureRandom.
        private const val GCM_TAG_LENGTH = 128                // Length of the GCM tag.
        private val DEFAULT_CHARSET = Charset.forName("UTF-8")  // Default charset to use with strings.

        // Initialize keyStore and key.
        private val keyStore = KeyStoreProvider.keyStore
        private val key = getKey()                      // The Key contains the AES key used to en-/decrypt.

        /**
         * Load the AES key from the KeyStore.
         * Generate a new key if it is not already on the KeyStore.
         * Key persists in the KeyStore as long as the app is not reinstalled or app data deleted.
         */
        private fun getKey(): SecretKey {
            //    Generate new key or load existing key.
            val key: SecretKey  // Will be assigned later.
            if (!keyStore.containsAlias(KEY_ALIAS)) {
                val keyGenerator = KeyGenerator.getInstance(
                    KeyProperties.KEY_ALGORITHM_AES,
                    KeyStoreProvider.KEY_STORE_TYPE
                )
                keyGenerator.init(
                    KeyGenParameterSpec.Builder(
                        KEY_ALIAS,
                        KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
                    ).setBlockModes(KeyProperties.BLOCK_MODE_GCM).setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE).setRandomizedEncryptionRequired(
                        false
                    ).build()
                )   // Init KeyGenerator with AES/GCM/NoPadding.
                key = keyGenerator.generateKey()
            } else {
                key = keyStore.getKey(KEY_ALIAS, null) as SecretKey
            }
            return key
        }

        /**
         * Encrypt a ByteArray to a ByteArray.
         * The result will contain the IV followed by the ciphertext.
         */
        fun encrypt(bytes: ByteArray): ByteArray {
            val c = Cipher.getInstance(AES_MODE)
            c.init(Cipher.ENCRYPT_MODE, key, SecureRandom())
            val encryptedBytes = c.doFinal(bytes)
            // Prefix ciphertext with IV. IV is needed for decryption.
            return c.iv + encryptedBytes // Appending arrays via +. Now that's neat!
        }

        /**
         * Encrypt a String to a ByteArray.
         * String to ByteArray conversion will assume UTF-8 charset.
         * The result will contain the IV followed by the ciphertext.
         */
        fun encrypt(clearText: String): ByteArray {
            val clearTextByte = clearText.toByteArray(DEFAULT_CHARSET)
            return encrypt(clearTextByte)
        }

        /**
         * Encrypt a ByteArray to a Base64 encoded String.
         * The result will contain the IV followed by the ciphertext.
         */
        fun encryptToBase64(bytes: ByteArray): String {
            return Base64.encodeToString(encrypt(bytes), Base64.DEFAULT)
        }

        /**
         * Encrypt a String to a Base64 encoded String.
         * String to ByteArray conversion will assume UTF-8 charset.
         * The result will contain the IV followed by the ciphertext.
         */
        fun encryptToBase64(clearText: String): String {
            return Base64.encodeToString(encrypt(clearText), Base64.DEFAULT)
        }

        /**
         * Decrypt a ByteArray to a ByteArray.
         * The encrypted input must start with the IV followed by the ciphertext.
         */
        fun decrypt(bytes: ByteArray): ByteArray {
            // Split bytes into IV and ciphertext.
            val iv = bytes.take(IV_LENGTH).toByteArray()
            val cipherText = bytes.drop(IV_LENGTH).toByteArray()

            val c = Cipher.getInstance(AES_MODE)
            c.init(
                Cipher.DECRYPT_MODE,
                key, GCMParameterSpec(GCM_TAG_LENGTH, iv)
            )
            return c.doFinal(cipherText)
        }

        /**
         * Decrypt a Base64 encoded string to a ByteArray.
         * The encrypted input must start with the IV followed by the ciphertext.
         */
        fun decrypt(cipherText: String): ByteArray {
            val bytes = Base64.decode(cipherText, Base64.DEFAULT)
            return decrypt(bytes)
        }

        /**
         * Decrypt a ByteArray to a String.
         * ByteArray to String conversion will use UTF-8 charset.
         * The encrypted input must start with the IV followed by the ciphertext.
         */
        fun decryptToString(bytes: ByteArray): String {
            return decrypt(bytes)
                .toString(DEFAULT_CHARSET)
        }

        /**
         * Decrypt a Base64 encoded String to a String.
         * Decoded ByteArray to String conversion will user UTF-8 charcode.
         * The encrypted input must start with the IV followed by the ciphertext.
         */
        fun decryptToString(cipherText: String): String {
            return decrypt(cipherText)
                .toString(DEFAULT_CHARSET)
        }
    }
}