/**
 * @author Anders Xiao
 * @date 2024-06-12
 */
package com.labijie.application.auth.controller

import com.labijie.application.SpringContext
import com.labijie.application.auth.model.OAuth2ClientEntry
import com.labijie.application.auth.oauth2.OAuth2UserToken
import com.labijie.application.auth.service.IOAuth2ClientUserService
import com.labijie.application.auth.service.getUserByOAuth2User
import com.labijie.application.auth.toUserDetails
import com.labijie.application.exception.UserNotFoundException
import com.labijie.infra.oauth2.AccessToken
import com.labijie.infra.oauth2.OAuth2ServerUtils.toAccessToken
import com.labijie.infra.oauth2.TwoFactorSignInHelper
import com.labijie.infra.oauth2.configuration.OAuth2ServerProperties
import com.labijie.infra.utils.ifNullOrBlank
import jakarta.annotation.security.PermitAll
import org.springframework.security.oauth2.client.registration.ClientRegistration
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestRedirectFilter
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController


@PermitAll
@RestController
@RequestMapping("/oauth2")
class OAuth2ClientController(
    private val oAuth2ServerProperties: OAuth2ServerProperties,
    private val signInHelper: TwoFactorSignInHelper,
    private val oauth2ClientUserService: IOAuth2ClientUserService
) {

    private val clients by lazy {
        SpringContext.current.getBean(ClientRegistrationRepository::class.java).let { repository ->
            val iterable = repository as? Iterable<*>

            val list = mutableListOf<OAuth2ClientEntry>()
            iterable?.let {
                it.forEach { client ->
                    if (client is ClientRegistration) {
                        val c = OAuth2ClientEntry(
                            client.registrationId,
                            client.clientName,
                            "${OAuth2AuthorizationRequestRedirectFilter.DEFAULT_AUTHORIZATION_REQUEST_BASE_URI}/${client.registrationId}"
                        )
                        list.add(c)
                    }
                }
            }
            list
        }
    }

    @GetMapping("/clients")
    fun listClients(): List<OAuth2ClientEntry> {
        return clients
    }

    @PostMapping("/client/exchange-token")
    fun exchangeToken(token: OAuth2UserToken): AccessToken {
        val user = oauth2ClientUserService.getUserByOAuth2User(token) ?: throw UserNotFoundException()

        val accessToken = signInHelper.signIn(oAuth2ServerProperties.defaultClient.clientId, user.toUserDetails())
        return accessToken.toAccessToken()
    }

}