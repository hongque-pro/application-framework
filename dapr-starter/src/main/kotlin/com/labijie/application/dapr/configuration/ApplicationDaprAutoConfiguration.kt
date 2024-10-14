package com.labijie.application.dapr.configuration

import com.labijie.application.JsonMode
import com.labijie.application.component.IMessageService
import com.labijie.application.configuration.DefaultsAutoConfiguration
import com.labijie.application.dapr.DaprDisposable
import com.labijie.application.dapr.IDaprClientBuildCustomizer
import com.labijie.application.dapr.PubsubSide
import com.labijie.application.dapr.components.DaprClusterEventPublisher
import com.labijie.application.dapr.components.DaprJsonSerializer
import com.labijie.application.dapr.components.DaprMessagePubService
import com.labijie.application.dapr.components.IClusterEventPublisher
import com.labijie.application.dapr.condition.ConditionalOnDaprPubsub
import com.labijie.application.dapr.localization.DaprClusterLocationEventListener
import com.labijie.application.dapr.localization.DaprLocalLocalizationListener
import com.labijie.application.service.ILocalizationService
import com.labijie.caching.ICacheManager
import com.labijie.infra.json.JacksonHelper
import com.labijie.infra.oauth2.resource.IResourceAuthorizationConfigurer
import com.labijie.infra.security.Rfc6238TokenService
import io.dapr.client.DaprClient
import io.dapr.client.DaprClientBuilder
import org.springframework.beans.factory.ObjectProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.AutoConfigureBefore
import org.springframework.boot.autoconfigure.condition.*
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Lazy

/**
 * @author Anders Xiao
 * @date 2023-12-04
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(DaprProperties::class)
@AutoConfigureBefore(DefaultsAutoConfiguration::class)
class ApplicationDaprAutoConfiguration {

    @ConditionalOnClass(name = ["com.labijie.infra.oauth2.resource.IResourceAuthorizationConfigurer"])
    @Configuration(proxyBeanMethods = false)
    protected class DaprResourceAutoConfiguration {

        @Bean
        fun daprResourceConfigurer(): IResourceAuthorizationConfigurer {
            return DaprResourceAuthorizationConfigurer()
        }
    }

    @Bean
    @Lazy
    @ConditionalOnMissingBean(DaprClient::class)
    fun daprClient(properties: DaprProperties, customizers: ObjectProvider<IDaprClientBuildCustomizer>): DaprClient {
        val objectMapper =
            if (properties.jsonMode == JsonMode.JAVASCRIPT) JacksonHelper.webCompatibilityMapper else JacksonHelper.defaultObjectMapper

        val objectSerializer = DaprJsonSerializer(objectMapper)
        val builder = DaprClientBuilder().withObjectSerializer(objectSerializer)
        customizers.orderedStream().forEach {
            it.customize(builder)
        }
        return builder.build()
    }

    @Bean
    @ConditionalOnMissingBean(DaprDisposable::class)
    fun daprModuleInitializer(daprClient: DaprClient, properties: DaprProperties): DaprDisposable {
        return DaprDisposable(daprClient, properties)
    }

    @ConditionalOnClass(name = ["com.labijie.application.component.IMessageService"])
    @Configuration(proxyBeanMethods = false)
    protected class DaprMessageAutoConfiguration {
        @Bean
        @Lazy
        @ConditionalOnMissingBean(IMessageService::class)
        @ConditionalOnDaprPubsub(side = PubsubSide.Pub)
        fun daprMessagePublisher(
            daprClient: DaprClient,
            properties: DaprProperties,
            cacheManager: ICacheManager,
            @Autowired(required = false)
            rfc6238TokenService: Rfc6238TokenService? = null
        ): DaprMessagePubService {
            return DaprMessagePubService(
                daprClient,
                properties,
                cacheManager,
                rfc6238TokenService ?: Rfc6238TokenService()
            )
        }
    }

    @Bean
    fun daprClusterEventPublisher(
        properties: DaprProperties,
        daprClient: DaprClient,
    ): DaprClusterEventPublisher {
        return DaprClusterEventPublisher(properties, daprClient)
    }


    @Configuration(proxyBeanMethods = false)
    @ConditionalOnProperty(
        name = ["application.dapr.cluster-event.subscribe-event"],
        havingValue = "true",
        matchIfMissing = false
    )
    @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
    protected class DaprClusterNotificationAutoConfig {

        @Bean
        fun daprLocalizationListener(clusterEventPublisher: IClusterEventPublisher): DaprLocalLocalizationListener {
            return DaprLocalLocalizationListener(clusterEventPublisher)
        }

        @Bean
        @ConditionalOnBean(ILocalizationService::class)
        fun daprLocalizationListener(localizationService: ILocalizationService): DaprClusterLocationEventListener {
            return DaprClusterLocationEventListener(localizationService)
        }
    }
}