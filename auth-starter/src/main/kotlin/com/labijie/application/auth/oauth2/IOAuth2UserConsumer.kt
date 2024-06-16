/**
 * @author Anders Xiao
 * @date 2024-06-12
 */
package com.labijie.application.auth.oauth2

import org.springframework.security.oauth2.client.registration.ClientRegistration
import org.springframework.security.oauth2.core.user.OAuth2User


interface IOAuth2UserConsumer {
    fun accept(client: ClientRegistration, user: OAuth2User): Boolean
}