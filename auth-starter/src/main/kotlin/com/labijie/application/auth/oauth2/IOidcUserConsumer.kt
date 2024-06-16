/**
 * @author Anders Xiao
 * @date 2024-06-12
 */
package com.labijie.application.auth.oauth2

import org.springframework.security.oauth2.client.registration.ClientRegistration
import org.springframework.security.oauth2.core.oidc.user.OidcUser


interface IOidcUserConsumer {
    fun accept(client: ClientRegistration, user: OidcUser): Boolean
}