/**
 * @author Anders Xiao
 * @date 2024-06-12
 */
package com.labijie.application.auth.configuration

import com.labijie.application.auth.component.OAuth2UserRegistrationIntegration
import com.labijie.application.auth.oauth2.OAuth2UserTokenArgumentResolver
import com.labijie.application.auth.service.IOAuth2ClientUserService
import com.labijie.application.auth.service.impl.DefaultOAuth2ClientUserService
import com.labijie.application.identity.service.IUserService
import com.labijie.infra.oauth2.configuration.OAuth2ServerAutoConfiguration
import com.labijie.infra.oauth2.resource.configuration.ResourceServerAutoConfiguration
import com.labijie.infra.oauth2.service.IOAuth2ServerOidcTokenService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.AutoConfigureAfter
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.transaction.support.TransactionTemplate
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer


@Configuration(proxyBeanMethods = false)
@AutoConfigureAfter(OAuth2ServerAutoConfiguration::class, ResourceServerAutoConfiguration::class)
@EnableConfigurationProperties(OAuth2ClientProperties::class)
class OAuth2LoginAutoConfiguration : WebMvcConfigurer {

    @Autowired
    private lateinit var oauth2ServerOidcTokenService: IOAuth2ServerOidcTokenService

    override fun addArgumentResolvers(
        resolvers: MutableList<HandlerMethodArgumentResolver>) {
        resolvers.add(OAuth2UserTokenArgumentResolver(oauth2ServerOidcTokenService))
    }


    @Bean
    @ConditionalOnMissingBean(IOAuth2ClientUserService::class)
    fun defaultOAuth2ClientUserService(
        userService: IUserService,
        transactionTemplate: TransactionTemplate
    ): DefaultOAuth2ClientUserService {
        return DefaultOAuth2ClientUserService(userService, transactionTemplate)
    }

    @Bean
    fun oauth2UserRegistrationIntegration(
        oauth2ClientUserService: IOAuth2ClientUserService,
        oAuth2ServerOidcTokenService: IOAuth2ServerOidcTokenService,
    ): OAuth2UserRegistrationIntegration {
        return OAuth2UserRegistrationIntegration(
            oAuth2ServerOidcTokenService,
            oauth2ClientUserService
        )
    }

}