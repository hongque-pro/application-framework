/**
 * @author Anders Xiao
 * @date 2024-06-12
 */
package com.labijie.application.auth.configuration

import com.labijie.application.auth.oauth2.OAuth2LoginCustomizer
import com.labijie.application.auth.oauth2.OAuth2UserParserUtilities
import com.labijie.infra.oauth2.resource.configuration.ResourceServerAutoConfiguration
import org.springframework.boot.autoconfigure.AutoConfigureBefore
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration(proxyBeanMethods = false)
@AutoConfigureBefore(ResourceServerAutoConfiguration::class)
class PreOAuth2LoginAutoConfiguration {

    @Bean
    fun oauth2UserParserUtilities(): OAuth2UserParserUtilities {
        return OAuth2UserParserUtilities()
    }

    @Bean
    fun oauth2LoginCustomizer(
        oauth2UserParser: OAuth2UserParserUtilities,
        authProperties: AuthProperties,
    ): OAuth2LoginCustomizer {
        return OAuth2LoginCustomizer(oauth2UserParser, authProperties)
    }
}