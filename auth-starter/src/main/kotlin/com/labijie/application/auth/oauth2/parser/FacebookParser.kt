/**
 * @author Anders Xiao
 * @date 2024-06-24
 */
package com.labijie.application.auth.oauth2.parser

import com.labijie.application.auth.oauth2.IOAuth2UserParser
import com.labijie.application.auth.oauth2.OAuth2UserToken
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Component


@Component
class FacebookParser : IOAuth2UserParser {
    override fun isSupported(clientRegistrationId: String): Boolean {
        return clientRegistrationId.equals("facebook", ignoreCase = true)
    }

    override fun parse(user: OAuth2User): OAuth2UserToken {
        return OAuth2UserToken().apply {
            this.email = user.attributes["email"]?.toString() ?: ""
            this.avatarUrl = user.attributes["avatar_url"]?.toString() ?: ""
            this.displayName = user.attributes["login"]?.toString() ?: ""
        }
    }
}