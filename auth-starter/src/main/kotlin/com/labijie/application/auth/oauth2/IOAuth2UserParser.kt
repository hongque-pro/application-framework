/**
 * @author Anders Xiao
 * @date 2024-06-17
 */
package com.labijie.application.auth.oauth2

import org.springframework.security.oauth2.core.user.OAuth2User


interface IOAuth2UserParser {
    fun isSupported(clientRegistrationId: String): Boolean
    fun parse(user: OAuth2User): OAuth2UserToken
}