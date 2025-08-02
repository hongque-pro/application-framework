/**
 * @author Anders Xiao
 * @date 2024-06-12
 */
package com.labijie.application.auth.configuration

import com.labijie.application.auth.oauth2.OAuth2LoginSuccessAndFailureCustomizer
import com.labijie.infra.oauth2.client.IOAuth2UserInfoLoader
import com.labijie.infra.oauth2.resource.configuration.ResourceServerAutoConfiguration
import com.labijie.infra.oauth2.service.IOAuth2ServerOidcTokenService
import org.springframework.boot.autoconfigure.AutoConfigureBefore
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration(proxyBeanMethods = false)
@AutoConfigureBefore(ResourceServerAutoConfiguration::class)
class PreResourceServerAutoConfiguration {


    @Bean
    fun oauth2LoginCustomizer(
        serverOidcTokenService: IOAuth2ServerOidcTokenService,
        authProperties: AuthProperties,
        oauth2UserInfoLoader: IOAuth2UserInfoLoader,
    ): OAuth2LoginSuccessAndFailureCustomizer {

        return OAuth2LoginSuccessAndFailureCustomizer(serverOidcTokenService, oauth2UserInfoLoader, authProperties)
    }
}