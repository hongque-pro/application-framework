/**
 * @author Anders Xiao
 * @date 2024-07-05
 */
package com.labijie.application.auth.component

import org.springframework.security.oauth2.client.registration.ClientRegistration
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository


class NoneClientRegistrationRepository : ClientRegistrationRepository, Iterable<ClientRegistration> {

    private val clients = emptySet<ClientRegistration>()
    override fun findByRegistrationId(registrationId: String?): ClientRegistration? {
        return null
    }

    override fun iterator(): Iterator<ClientRegistration> {
        return clients.iterator()
    }
}