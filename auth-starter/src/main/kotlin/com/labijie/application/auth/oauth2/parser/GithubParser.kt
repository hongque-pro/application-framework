/**
 * @author Anders Xiao
 * @date 2024-06-17
 */
package com.labijie.application.auth.oauth2.parser

import com.labijie.application.auth.oauth2.IOAuth2UserParser
import com.labijie.application.auth.oauth2.OAuth2UserToken
import com.labijie.application.propertiesFrom
import org.springframework.core.Ordered
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Component


/**
 * refer: https://docs.github.com/en/rest/users/users
 */
@Component
class GithubParser : IOAuth2UserParser {
    override fun isSupported(clientRegistrationId: String): Boolean {
        return clientRegistrationId.lowercase() == "github"
    }

    override fun parse(user: OAuth2User): OAuth2UserToken {
        val token = OAuth2UserToken().apply {
            this.email = user.attributes["email"]?.toString() ?: ""
            this.avatarUrl = user.attributes["avatar_url"]?.toString() ?: ""
            this.displayName = user.attributes["login"]?.toString() ?: ""
        }

        return token
    }

    override fun getOrder(): Int {
        return Int.MAX_VALUE
    }

}