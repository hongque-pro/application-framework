/**
 * @author Anders Xiao
 * @date 2024-06-12
 */
package com.labijie.application.auth.oauth2

import com.labijie.application.SpringContext
import com.labijie.application.auth.configuration.AuthProperties
import com.labijie.infra.oauth2.component.IOAuth2ServerRSAKeyPair
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.apache.hc.core5.net.URIBuilder
import org.slf4j.LoggerFactory
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken
import org.springframework.security.oauth2.client.registration.ClientRegistration
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository
import org.springframework.security.oauth2.core.oidc.user.OidcUser
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.security.web.authentication.AuthenticationFailureHandler
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import java.io.IOException


class OAuth2ClientAuthenticationCompleteHandler(
    private val oauth2UserParserUtilities: OAuth2UserParserUtilities,
    private val authProperties: AuthProperties) :
    AuthenticationSuccessHandler,
    AuthenticationFailureHandler {

    companion object {
            private val logger by lazy {
                LoggerFactory.getLogger(OAuth2ClientAuthenticationCompleteHandler::class.java)
            }
    }

    private val oauth2UserConsumers by lazy {
        SpringContext.current.getBeanProvider(IOAuth2UserConsumer::class.java)
    }

    private val oidcUserConsumers by lazy {
        SpringContext.current.getBeanProvider(IOidcUserConsumer::class.java)
    }

    private val clientRegistrationRepository by lazy {
        SpringContext.current.getBean(ClientRegistrationRepository::class.java)
    }

    private val rsaKeys by lazy {
        SpringContext.current.getBean(IOAuth2ServerRSAKeyPair::class.java)
    }

    @Throws(IOException::class, ServletException::class)
    override fun onAuthenticationSuccess(
        request: HttpServletRequest?,
        response: HttpServletResponse?,
        authentication: Authentication
    ) {
        if (authentication is OAuth2AuthenticationToken) {
            val clientRegistration =
                clientRegistrationRepository.findByRegistrationId(authentication.authorizedClientRegistrationId)

            if (authentication.principal is OidcUser) {
                authentication.authorizedClientRegistrationId
                consumeOidc2User(clientRegistration, authentication.principal as OidcUser)
            } else if (authentication.principal is OAuth2User) {
                consumeOAuth2User(clientRegistration, authentication.principal as OAuth2User)
            } else {
                throw BadCredentialsException("Principal is not an oauth2 user")
            }

            val uri = URIBuilder(authProperties.oauth2Login.redirectionUri).apply {
                val userToken = oauth2UserParserUtilities.parse(clientRegistration, authentication.principal)


                if (!userToken.avatarUrl.isNullOrBlank()) {
                    this.addParameter("avatar_url", userToken.avatarUrl)
                }
                if (!userToken.email.isNullOrBlank()) {
                    this.addParameter("email", userToken.email)
                }
                if (!userToken.displayName.isNullOrBlank()) {
                    this.addParameter("display_name", userToken.displayName)
                }
                this.addParameter("provider", clientRegistration.clientName)
                this.addParameter(
                    OAuth2UserTokenArgumentResolver.TOKEN_PARAMETER_NAME,
                    userToken.serializeToken(rsaKeys.getPublicKey(), authProperties.securitySecretKey)
                )

            }.build()
            logger.info("Complete oauth2 client sign-in, redirect to:\n${uri}")
            response?.sendRedirect(uri.toString())
        }
    }

    private fun consumeOAuth2User(client: ClientRegistration, user: OAuth2User) {
        for (c in oauth2UserConsumers.orderedStream()) {
            if (c.accept(client, user)) {
                break
            }
        }
    }

    private fun consumeOidc2User(client: ClientRegistration, user: OidcUser) {
        for (c in oidcUserConsumers.orderedStream()) {
            if (c.accept(client, user)) {
                break
            }
        }
    }

    override fun onAuthenticationFailure(
        request: HttpServletRequest?,
        response: HttpServletResponse?,
        exception: AuthenticationException?
    ) {
        logger.error("An error occurred while exchanging data with OAuth2 website.", exception)

        val uri = URIBuilder(authProperties.oauth2Login.redirectionUri).apply {
            this.addParameter("error", "oath2")
        }.build()
        response?.sendRedirect(uri.toString())
    }


}