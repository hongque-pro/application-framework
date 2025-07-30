/**
 * @author Anders Xiao
 * @date 2024-06-12
 */
package com.labijie.application.auth.oauth2

import com.labijie.application.auth.configuration.AuthProperties
import com.labijie.application.auth.service.IOAuth2UserTokenCodec
import com.labijie.infra.oauth2.client.IOAuth2UserInfoLoader
import com.labijie.infra.oauth2.client.extension.IOAuth2LoginCustomizer
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configurers.oauth2.client.OAuth2LoginConfigurer


class OAuth2LoginSuccessAndFailureCustomizer(
    private val oauth2UserCodec: IOAuth2UserTokenCodec,
    private val oauth2UserInfoLoader: IOAuth2UserInfoLoader,
    private val authProperties: AuthProperties
) : IOAuth2LoginCustomizer {

    override fun customize(configure: OAuth2LoginConfigurer<HttpSecurity>) {

        val handler = OAuth2ClientAuthenticationCompleteHandler(
            oauth2UserCodec,
            oauth2UserInfoLoader,
            authProperties
        )

        configure.let {
            it.successHandler(handler)
            it.failureHandler(handler)
        }
    }
}