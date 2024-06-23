/**
 * @author Anders Xiao
 * @date 2024-06-18
 */
package com.labijie.application.auth.oauth2.parser

import com.labijie.application.auth.oauth2.IOAuth2UserParser
import com.labijie.application.auth.oauth2.OAuth2UserToken
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Component


@Component
class GoogleParser : IOAuth2UserParser {
    override fun isSupported(clientRegistrationId: String): Boolean {
        return clientRegistrationId.lowercase() == "google"
    }

    override fun parse(user: OAuth2User): OAuth2UserToken {
        val token = OAuth2UserToken().apply {
            val emailVerified = (user.attributes["email_verified"] as? Boolean) ?: false
            if(emailVerified) {
                this.email = user.attributes["email"]?.toString() ?: ""
            }
            this.avatarUrl = user.attributes["picture"]?.toString() ?: ""
            this.displayName = user.attributes["name"]?.toString() ?: ""
        }

        return token
    }

    override fun getOrder(): Int {
        return Int.MAX_VALUE
    }
}