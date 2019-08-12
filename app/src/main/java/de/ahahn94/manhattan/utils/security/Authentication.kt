package de.ahahn94.manhattan.utils.security

import android.util.Base64
import java.nio.charset.Charset

/**
 * Class that handles generation of the authentication headers.
 */
class Authentication {

    companion object {

        /**
         * Generate an authentication string for a HTTP Basic Authorization header.
         * Returns a string consisting of "Basic " followed by $username:$password in Base64 encoding.
         */
        fun generateBasicAuthHeader(username: String, password: String): String {
            return "Basic " + Base64.encodeToString(
                "$username:$password".toByteArray(Charset.forName("UTF-8")),
                Base64.NO_WRAP
            )
        }

        /**
         * Generate an authentication string for a Bearer Token Authorization header.
         * Returns a string consisting of "Bearer " followed by the token.
         */
        fun generateBearerTokenHeader(token: String): String {
            return "Bearer $token"
        }

    }

}