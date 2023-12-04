package com.labijie.application.dapr.configuration

import com.labijie.application.component.IMessageService
import com.labijie.application.configuration.DefaultsAutoConfiguration
import com.labijie.application.dapr.PubsubSide
import com.labijie.application.dapr.components.DaprMessagePubService
import com.labijie.application.dapr.condition.ConditionalOnDaprPubsub
import com.labijie.application.web.antMatchers
import com.labijie.caching.ICacheManager
import com.labijie.infra.oauth2.resource.IResourceAuthorizationConfigurer
import com.labijie.infra.security.Rfc6238TokenService
import org.springframework.boot.autoconfigure.AutoConfigureBefore
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher
import org.springframework.web.servlet.handler.HandlerMappingIntrospector

/**
 * @author Anders Xiao
 * @date 2023-12-04
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(DaprProperties::class, DaprMessageServiceProperties::class)
@AutoConfigureBefore(DefaultsAutoConfiguration::class)
class ApplicationDaprAutoConfiguration : IResourceAuthorizationConfigurer {

    override fun configure(registry: AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry) {
        registry.antMatchers(
            "/actors/**",
            "/healthz",
            "/dapr/**"
        ).permitAll()
    }

    @Bean
    @ConditionalOnMissingBean(IMessageService::class)
    @ConditionalOnDaprPubsub(side = PubsubSide.Pub)
    fun daprMessagePublisher(
        daprMsgSvcProperties: DaprMessageServiceProperties,
        cacheManager: ICacheManager,
        rfc6238TokenService: Rfc6238TokenService
    ): DaprMessagePubService {
        return DaprMessagePubService(daprMsgSvcProperties, cacheManager, rfc6238TokenService)
    }



}