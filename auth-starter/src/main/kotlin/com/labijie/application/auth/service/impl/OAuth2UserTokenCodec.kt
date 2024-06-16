/**
 * @author Anders Xiao
 * @date 2024-06-16
 */
package com.labijie.application.auth.service.impl

import com.labijie.application.auth.configuration.AuthProperties
import com.labijie.application.auth.exception.InvalidOAuth2UserToken
import com.labijie.application.auth.oauth2.OAuth2UserToken
import com.labijie.application.auth.service.IOAuth2UserTokenCodec
import com.labijie.infra.oauth2.component.IOAuth2ServerRSAKeyPair
import com.labijie.infra.utils.throwIfNecessary


class OAuth2UserTokenCodec(
    private val authProperties: AuthProperties,
    private val oauth2ServerRSAKeyPair: IOAuth2ServerRSAKeyPair,
) : IOAuth2UserTokenCodec {
    override fun encode(token: OAuth2UserToken): String {
        return token.serializeToken(oauth2ServerRSAKeyPair.getPublicKey(), authProperties.securitySecretKey)
    }

    override fun decode(tokenValue: String, check: Boolean): OAuth2UserToken {

        if(tokenValue.isBlank()){
            throw InvalidOAuth2UserToken()
        }

        val oauth2UserToken = try {
            OAuth2UserToken.deserializeToken(tokenValue, authProperties.securitySecretKey, oauth2ServerRSAKeyPair.getPrivateKey())
        }catch (e: Throwable) {
            e.throwIfNecessary()
            if(e is InvalidOAuth2UserToken) {
                throw e
            }
            throw InvalidOAuth2UserToken(cause = e)
        }

        if(check) {
            if (oauth2UserToken.id.isBlank()) {
                throw InvalidOAuth2UserToken()
            }

            if (System.currentTimeMillis() > oauth2UserToken.expired) {
                throw InvalidOAuth2UserToken()
            }
        }

        return oauth2UserToken
    }
}