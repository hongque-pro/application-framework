/**
 * @author Anders Xiao
 * @date 2024-06-13
 */
package com.labijie.application.auth.oauth2

import com.labijie.application.auth.configuration.AuthProperties
import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientProperties
import org.springframework.security.config.oauth2.client.CommonOAuth2Provider
import org.springframework.security.oauth2.client.registration.ClientRegistration


object ClientRegistrationBuilder {
    private fun getProvider(
        clientProperties: OAuth2ClientProperties,
        providerName: String
    ): OAuth2ClientProperties.Registration? {
        val r = clientProperties.registration[providerName]
        if (r != null && !r.clientId.isNullOrBlank() && !r.clientId.isNullOrBlank()) {
            return r
        }
        return null
    }

    private fun ClientRegistration.Builder.applyCommons(
        authProperties: AuthProperties,
        registration: OAuth2ClientProperties.Registration
    ): ClientRegistration.Builder {
        val baseUrl = authProperties.oauth2Login.redirectionBaseUrl.let {
            if (it.endsWith("/")) {
                it.trimEnd('/')
            } else {
                it
            }
        }

        if (registration.redirectUri.isNullOrBlank()) {
            if (baseUrl.isNotBlank()) {
                this.redirectUri("${baseUrl}/{action}/oauth2/code/{registrationId}")
            }
        } else {
            var uri = registration.redirectUri
            if (baseUrl.isNotBlank()) {
                uri = uri.replace("{baseUrl}", baseUrl)
            }
            this.redirectUri(uri)
        }

        if (!registration.scope.isNullOrEmpty()) {
            this.scope(registration.scope)
        }

        return this
    }

    fun build(authProperties: AuthProperties, clientProperties: OAuth2ClientProperties): List<ClientRegistration> {
        val clientRegistrations = mutableListOf<ClientRegistration>()

        //https://developers.google.com/identity/protocols/oauth2/scopes
        getProvider(clientProperties, "google")?.let {
            clientRegistrations.add(
                CommonOAuth2Provider.GOOGLE.getBuilder("google")
                    //.scope("openid", "https://www.googleapis.com/auth/userinfo.profile", "https://www.googleapis.com/auth/userinfo.email")
                    .clientId(it.clientId)
                    .clientSecret(it.clientSecret)
                    .applyCommons(authProperties, it)
                    .build()
            )
        }

        //https://docs.github.com/en/apps/oauth-apps/building-oauth-apps/scopes-for-oauth-apps
        getProvider(clientProperties, "github")?.let {
            clientRegistrations.add(
                CommonOAuth2Provider.GITHUB.getBuilder("github")
                    .scope("read:user", "user:email")
                    .clientId(it.clientId)
                    .clientSecret(it.clientSecret)
                    .applyCommons(authProperties, it)
                    .build()
            )
        }

        getProvider(clientProperties, "facebook")?.let {
            clientRegistrations.add(
                CommonOAuth2Provider.FACEBOOK.getBuilder("facebook")
                    .clientId(it.clientId)
                    .clientSecret(it.clientSecret)
                    .applyCommons(authProperties, it)
                    .build()
            )
        }
        return clientRegistrations
    }
}