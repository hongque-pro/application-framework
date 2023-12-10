package com.labijie.application.dapr.configuration

import com.labijie.application.component.IMessageService
import com.labijie.application.configuration.DefaultsAutoConfiguration
import com.labijie.application.dapr.IDaprClientBuildCustomizer
import com.labijie.application.dapr.PubsubSide
import com.labijie.application.dapr.components.DaprMessagePubService
import com.labijie.application.dapr.condition.ConditionalOnDaprPubsub
import com.labijie.application.web.antMatchers
import com.labijie.caching.ICacheManager
import com.labijie.infra.oauth2.resource.IResourceAuthorizationConfigurer
import com.labijie.infra.security.Rfc6238TokenService
import io.dapr.client.DaprClient
import io.dapr.client.DaprClientBuilder
import org.springframework.beans.factory.ObjectProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.AutoConfigureBefore
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Lazy
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer

/**
 * @author Anders Xiao
 * @date 2023-12-04
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(DaprProperties::class)
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
        daprProperties: DaprProperties,
        cacheManager: ICacheManager,
        @Autowired(required = false)
        rfc6238TokenService: Rfc6238TokenService? = null
    ): DaprMessagePubService {
        return DaprMessagePubService(daprProperties, cacheManager, rfc6238TokenService ?: Rfc6238TokenService())
    }


    @Bean
    @Lazy
    @ConditionalOnMissingBean(DaprClient::class)
    fun daprClient(customizers: ObjectProvider<IDaprClientBuildCustomizer>) : DaprClient {
        val builder = DaprClientBuilder()

        customizers.orderedStream().forEach {
            it.customize(builder)
        }
        return builder.build()
    }
}