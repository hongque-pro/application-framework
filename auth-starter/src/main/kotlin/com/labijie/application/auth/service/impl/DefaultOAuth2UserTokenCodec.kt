/**
 * @author Anders Xiao
 * @date 2024-06-16
 */
package com.labijie.application.auth.service.impl

import com.labijie.application.HttpFormUrlCodec
import com.labijie.application.auth.configuration.AuthProperties
import com.labijie.application.auth.exception.ExpiredOAuth2UserTokenException
import com.labijie.application.auth.exception.InvalidOAuth2UserTokenException
import com.labijie.application.auth.exception.OAuth2UserTokenException
import com.labijie.application.auth.service.IOAuth2UserTokenCodec
import com.labijie.application.crypto.RsaUtils
import com.labijie.application.letIfNotBlank
import com.labijie.infra.oauth2.client.StandardOidcUser
import com.labijie.infra.oauth2.component.IOAuth2ServerRSAKeyPair
import com.labijie.infra.utils.ifNullOrBlank
import com.labijie.infra.utils.throwIfNecessary
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import java.time.Duration
import java.time.Instant


class DefaultOAuth2UserTokenCodec(
    private val authProperties: AuthProperties,
    private val oauth2ServerRSAKeyPair: IOAuth2ServerRSAKeyPair,
) : IOAuth2UserTokenCodec {

    companion object{

        private fun deserializeToken(tokenValue: String, secret: String, rsaPrivateKey: RSAPrivateKey): StandardOidcUser {
            val formUrl = RsaUtils.decrypt(tokenValue, rsaPrivateKey)
            val values = HttpFormUrlCodec.decode(formUrl)

            if(!values.contains("s") && values.getFirst("s") != secret) {
                throw InvalidOAuth2UserTokenException()
            }

            if(!values.contains("i")) {
                throw InvalidOAuth2UserTokenException()
            }

            if(!values.contains("pd")) {
                throw InvalidOAuth2UserTokenException()
            }

            val timeExpired = values.getFirst("ex")?.toLongOrNull() ?: throw InvalidOAuth2UserTokenException()
            if(Instant.ofEpochSecond(timeExpired).isBefore(Instant.now())) {
                throw ExpiredOAuth2UserTokenException()
            }

            val standardOidcUser = StandardOidcUser(
                values.getFirst("pd").orEmpty(),
                values.getFirst("i").orEmpty())

            return standardOidcUser.apply {
                this.picture = values.getFirst("p")
                this.username = values.getFirst("m")
                this.email = values.getFirst("e")?.also {
                    this.emailVerified = values.getFirst("ev").ifNullOrBlank { "false" }.toBooleanStrictOrNull() ?: false
                    this.emailHidden = values.getFirst("ei")?.toBooleanStrictOrNull()
                    it
                }
                this.clientId = values.getFirst("ci")
            }
        }

        private fun StandardOidcUser.serializeToken(rsaPublicKey: RSAPublicKey, secret: String, expiration: Duration): String {

            val map = mutableMapOf<String, String>(
                "i" to this.userId,
                "ex" to (Instant.now().epochSecond + expiration.toSeconds()).toString(),
                "s" to secret,
                "pd" to this.provider
            )

            this.picture?.letIfNotBlank {
                map.putIfAbsent("p", it)
            }
            this.username?.letIfNotBlank {
                map.putIfAbsent("n", it)
            }
            this.email?.letIfNotBlank {
                map.putIfAbsent("e", it)
                if(emailVerified) {
                    map.putIfAbsent("ev", true.toString())
                }
                if(emailHidden == true) {
                    map.putIfAbsent("ei", true.toString())
                }
            }
            this.clientId?.letIfNotBlank {
                map.putIfAbsent("ci", it)
            }

            val signData = HttpFormUrlCodec.encode(map)

            val token = RsaUtils.encrypt(signData, rsaPublicKey)
            return token
        }
    }


    override fun encode(token: StandardOidcUser, expiration: Duration): String {
        return token.serializeToken(oauth2ServerRSAKeyPair.getPublicKey(), authProperties.securitySecretKey, expiration)
    }

    override fun decode(tokenValue: String, check: Boolean): StandardOidcUser {

        if(tokenValue.isBlank()){
            throw InvalidOAuth2UserTokenException()
        }

        val oauth2UserToken = try {
            deserializeToken(tokenValue, authProperties.securitySecretKey, oauth2ServerRSAKeyPair.getPrivateKey())
        }catch (e: Throwable) {
            e.throwIfNecessary()
            if(e is OAuth2UserTokenException) {
                throw e
            }
            throw InvalidOAuth2UserTokenException(cause = e)
        }

        if(check) {
            if (oauth2UserToken.userId.isBlank()) {
                throw InvalidOAuth2UserTokenException()
            }
        }

        return oauth2UserToken
    }
}