/**
 * @author Anders Xiao
 * @date 2024-06-17
 */
package com.labijie.application.auth.event

import com.labijie.application.auth.oauth2.OAuth2UserTokenArgumentResolver
import com.labijie.application.auth.service.IOAuth2ClientUserService
import com.labijie.application.auth.service.IOAuth2UserTokenCodec
import com.labijie.infra.oauth2.authentication.OAuth2EndpointUtils
import com.labijie.infra.oauth2.events.UserSignedInEvent
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationListener
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings
import java.net.URI


class OAuth2ClientSignIngEvenListener(
    private val oauth2UserTokenCodec: IOAuth2UserTokenCodec,
    private val oauth2ClientUserService: IOAuth2ClientUserService,
    private val oauth2ServerSettings: AuthorizationServerSettings
) : ApplicationListener<UserSignedInEvent> {

    companion object {
        private val logger by lazy {
            LoggerFactory.getLogger(OAuth2ClientSignIngEvenListener::class.java)
        }
    }

    override fun onApplicationEvent(event: UserSignedInEvent) {
        val request = event.httpServletRequest
        if(request != null && URI(request.requestURI).path.equals(oauth2ServerSettings.tokenEndpoint)) {
            val userId = event.principle.userId.toLong()

            val requestParams =
                event.httpServletRequest?.parameterMap?.get(OAuth2UserTokenArgumentResolver.TOKEN_PARAMETER_NAME)
                    ?.firstOrNull()

            val tokenValue =
                if (requestParams.isNullOrBlank()) event.httpServletRequest?.getHeader(OAuth2UserTokenArgumentResolver.TOKEN_PARAMETER_NAME) else requestParams

            if (!tokenValue.isNullOrBlank()) {
                val oauth2UserToken = oauth2UserTokenCodec.decode(tokenValue, false)
                logger.info("Got oauth2 user (provider: ${oauth2UserToken.clientRegistrationId}) from sign in http request.")
                oauth2ClientUserService.addOAuth2UserLoginToUser(userId, oauth2UserToken)
            }
        }
    }
}