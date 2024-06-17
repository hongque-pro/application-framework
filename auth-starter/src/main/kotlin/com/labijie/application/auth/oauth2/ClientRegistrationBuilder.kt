/**
 * @author Anders Xiao
 * @date 2024-06-13
 */
package com.labijie.application.auth.oauth2

import org.springframework.core.env.Environment
import org.springframework.security.config.oauth2.client.CommonOAuth2Provider
import org.springframework.security.oauth2.client.registration.ClientRegistration


object ClientRegistrationBuilder {
    private data class LoginProvideConfig(val clientId: String, val clientSecret: String)

    private fun getProvider(environment: Environment, providerName: String): LoginProvideConfig? {
        val clientId = environment.getProperty("spring.security.oauth2.client.registration.${providerName}.client-id")
        val clientSecret = environment.getProperty("spring.security.oauth2.client.registration.${providerName}.client-secret")

        if(!clientId.isNullOrBlank() && !clientSecret.isNullOrBlank()) {
            return LoginProvideConfig(clientId, clientSecret)
        }
        return null
    }

    fun build(environment: Environment): List<ClientRegistration> {
        val clientRegistrations = mutableListOf<ClientRegistration>()

        //https://developers.google.com/identity/protocols/oauth2/scopes
        getProvider(environment, "google")?.let {
            clientRegistrations.add(
                CommonOAuth2Provider.GOOGLE.getBuilder("google")
                    //.scope("OpenID", "https://www.googleapis.com/auth/userinfo.profile", "https://www.googleapis.com/auth/userinfo.email")
                    .clientId(it.clientId)
                    .clientSecret(it.clientSecret)
                    .build()
            )
        }

        //https://docs.github.com/en/apps/oauth-apps/building-oauth-apps/scopes-for-oauth-apps
        getProvider(environment, "github")?.let {
            clientRegistrations.add(
                CommonOAuth2Provider.GITHUB.getBuilder("github")
                    .scope("read:user", "user:email")
                    .clientId(it.clientId)
                    .clientSecret(it.clientSecret)
                    .build()
            )
        }

        getProvider(environment, "facebook")?.let {
            clientRegistrations.add(
                CommonOAuth2Provider.FACEBOOK.getBuilder("facebook")
                    .clientId(it.clientId)
                    .clientSecret(it.clientSecret)
                    .build()
            )
        }
        return clientRegistrations
    }
}