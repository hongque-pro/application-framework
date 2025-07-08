/**
 * @author Anders Xiao
 * @date 2024-06-12
 */
package com.labijie.application.auth.oauth2

import com.labijie.application.HttpFormUrlCodec
import com.labijie.application.auth.exception.InvalidOAuth2UserToken
import com.labijie.application.crypto.RsaUtils
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import java.time.Duration


class OAuth2UserToken {
    var id: String = ""
    var clientRegistrationId: String = ""
    var avatarUrl: String? = ""
    var displayName: String? = ""
    var email: String? = ""
    var expired: Long = System.currentTimeMillis() + Duration.ofHours(1).toMillis()

    companion object {
        fun deserializeToken(tokenValue: String, secret: String, rsaPrivateKey: RSAPrivateKey): OAuth2UserToken {
            val formUrl = RsaUtils.decrypt(tokenValue, rsaPrivateKey)
            val values = HttpFormUrlCodec.decode(formUrl)

            if(values.getFirst("s") != secret) {
                throw InvalidOAuth2UserToken()
            }
            return OAuth2UserToken().apply {
                this.id = values.getFirst("i") ?: ""
                this.avatarUrl = values.getFirst("a")
                this.displayName = values.getFirst("d")
                this.email = values.getFirst("e")
                this.clientRegistrationId = values.getFirst("c") ?: ""
                this.expired = (values.getFirst("ex") ?: "").toLongOrNull() ?: 0
            }
        }
    }

    fun serializeToken(rsaPublicKey: RSAPublicKey, secret: String): String {
        val map = mapOf(
            "i" to this.id,
            "a" to this.avatarUrl,
            "d" to this.displayName,
            "e" to this.email,
            "ex" to this.expired.toString(),
            "s" to secret,
            "c" to clientRegistrationId
        )

        val signData = HttpFormUrlCodec.encode(map)

        val token = RsaUtils.encrypt(signData, rsaPublicKey)
        return token
    }


}