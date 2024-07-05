/**
 * @author Anders Xiao
 * @date 2024-06-12
 */
package com.labijie.application.auth.configuration

import com.labijie.application.auth.component.NoneClientRegistrationRepository
import com.labijie.application.auth.component.OAuth2UserRegistrationIntegration
import com.labijie.application.auth.controller.OAuth2ClientController
import com.labijie.application.auth.event.OAuth2ClientSignIngEvenListener
import com.labijie.application.auth.oauth2.ClientRegistrationBuilder
import com.labijie.application.auth.oauth2.OAuth2UserTokenArgumentResolver
import com.labijie.application.auth.oauth2.parser.GithubParser
import com.labijie.application.auth.service.IOAuth2ClientUserService
import com.labijie.application.auth.service.IOAuth2UserTokenCodec
import com.labijie.application.auth.service.impl.OAuth2ClientUserService
import com.labijie.application.auth.service.impl.OAuth2UserTokenCodec
import com.labijie.application.identity.service.IUserService
import com.labijie.infra.oauth2.component.IOAuth2ServerRSAKeyPair
import com.labijie.infra.oauth2.resource.configuration.ResourceServerAutoConfiguration
import org.springframework.boot.autoconfigure.AutoConfigureAfter
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.core.env.Environment
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings
import org.springframework.transaction.support.TransactionTemplate
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer


@Configuration(proxyBeanMethods = false)
@AutoConfigureAfter(ResourceServerAutoConfiguration::class)
@ComponentScan(basePackageClasses = [GithubParser::class])
class OAuth2LoginAutoConfiguration : WebMvcConfigurer {
    override fun addArgumentResolvers(resolvers: MutableList<HandlerMethodArgumentResolver>) {
        resolvers.add(OAuth2UserTokenArgumentResolver())
    }

    @Bean
    @ConditionalOnMissingBean(ClientRegistrationRepository::class)
    fun clientRegistrationRepository(environment: Environment): ClientRegistrationRepository {
        val clients = ClientRegistrationBuilder.build(environment);
        return if(clients.isEmpty()) NoneClientRegistrationRepository() else InMemoryClientRegistrationRepository(clients)
    }

    @Bean
    @ConditionalOnMissingBean(IOAuth2UserTokenCodec::class)
    fun oauth2UserTokenCodec(
        authProperties: AuthProperties,
        oauth2ServerRSAKeyPair: IOAuth2ServerRSAKeyPair
    ): OAuth2UserTokenCodec {
        return OAuth2UserTokenCodec(authProperties, oauth2ServerRSAKeyPair)
    }

    @Bean
    @ConditionalOnMissingBean(IOAuth2ClientUserService::class)
    fun oauth2ClientUserService(
        userService: IUserService,
        clientRegistrationRepository: ClientRegistrationRepository,
        transactionTemplate: TransactionTemplate
    ): OAuth2ClientUserService {
        return OAuth2ClientUserService(userService, clientRegistrationRepository, transactionTemplate)
    }

    @Configuration(proxyBeanMethods = false)
    @Import(OAuth2ClientController::class)
    protected class OAuth2ClientControllerImporter

    @Bean
    fun oauth2UserRegistrationIntegration(
        oauth2ClientUserService: IOAuth2ClientUserService,
        oauth2UserTokenCodec: IOAuth2UserTokenCodec
    ): OAuth2UserRegistrationIntegration {
        return OAuth2UserRegistrationIntegration(
            oauth2UserTokenCodec,
            oauth2ClientUserService
        )
    }

    @Bean
    fun oauth2ClientSignIngEvenListener(
        oauth2ClientUserService: IOAuth2ClientUserService,
        oauth2UserTokenCodec: IOAuth2UserTokenCodec,
        oauth2ServerSettings: AuthorizationServerSettings,
    ): OAuth2ClientSignIngEvenListener {
        return OAuth2ClientSignIngEvenListener(oauth2UserTokenCodec, oauth2ClientUserService, oauth2ServerSettings)
    }
}