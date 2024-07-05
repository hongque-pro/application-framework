/**
 * @author Anders Xiao
 * @date 2024-06-12
 */
package com.labijie.application.auth.oauth2

import com.labijie.application.auth.configuration.AuthProperties
import com.labijie.infra.oauth2.resource.component.IOAuth2LoginCustomizer
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configurers.oauth2.client.OAuth2LoginConfigurer


class OAuth2LoginCustomizer(
    private val parserUtilities: OAuth2UserParserUtilities,
    private val authProperties: AuthProperties
) : IOAuth2LoginCustomizer {
    override fun customize(t: OAuth2LoginConfigurer<HttpSecurity>) {
        val handler = OAuth2ClientAuthenticationCompleteHandler(parserUtilities, authProperties)

        t.let {
            it.successHandler(handler)
            it.failureHandler(handler)
        }
    }
}