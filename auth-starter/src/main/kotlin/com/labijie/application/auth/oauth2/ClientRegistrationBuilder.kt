/**
 * @author Anders Xiao
 * @date 2024-06-13
 */
package com.labijie.application.auth.oauth2

import com.labijie.application.auth.configuration.AuthProperties
import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientProperties
import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientPropertiesMapper
import org.springframework.core.env.Environment
import org.springframework.security.config.oauth2.client.CommonOAuth2Provider
import org.springframework.security.oauth2.client.registration.ClientRegistration


object ClientRegistrationBuilder {
    private data class LoginProvideConfig(val clientId: String, val clientSecret: String)

    private fun getProvider(clientProperties: OAuth2ClientProperties, providerName: String): OAuth2ClientProperties.Registration? {
        val r = clientProperties.registration[providerName]
        if(r != null && !r.clientId.isNullOrBlank() && !r.clientId.isNullOrBlank()) {
            return r
        }
        return null
    }

    private fun ClientRegistration.Builder.applyCommons(authProperties: AuthProperties, registration: OAuth2ClientProperties.Registration): ClientRegistration.Builder {
        val baseUrl = authProperties.oauth2Login.redirectionBaseUrl
        val redirectUri =  if(baseUrl.isNotBlank()) {
            "${baseUrl}/{action}/oauth2/code/{registrationId}"
        }else {
            ""
        }

        if(redirectUri.isNotBlank()) {
            this.redirectUri(redirectUri)
        }

        if(!registration.scope.isNullOrEmpty()) {
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