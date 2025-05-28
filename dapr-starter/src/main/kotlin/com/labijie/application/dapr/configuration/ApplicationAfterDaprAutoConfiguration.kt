package com.labijie.application.dapr.configuration

import com.labijie.application.JsonMode
import com.labijie.application.component.IBootPrinter
import com.labijie.application.component.IMessageService
import com.labijie.application.dapr.IDaprClientBuildCustomizer
import com.labijie.application.dapr.PubsubSide
import com.labijie.application.dapr.components.DaprClusterEventPublisher
import com.labijie.application.dapr.components.DaprJsonSerializer
import com.labijie.application.dapr.components.DaprMessagePubService
import com.labijie.application.dapr.components.IClusterEventPublisher
import com.labijie.application.dapr.condition.ConditionalOnDaprPubsub
import com.labijie.application.dapr.localization.LocalLocalizationEventListener
import com.labijie.caching.ICacheManager
import com.labijie.infra.json.JacksonHelper
import com.labijie.infra.security.Rfc6238TokenService
import io.dapr.Topic
import io.dapr.client.DaprClient
import io.dapr.client.DaprClientBuilder
import io.dapr.springboot.DaprAutoConfiguration
import org.springdoc.core.models.GroupedOpenApi
import org.springframework.beans.factory.ObjectProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.AutoConfigureAfter
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Lazy

/**
 * @author Anders Xiao
 * @date 2023-12-04
 */
@Configuration(proxyBeanMethods = false)
@AutoConfigureAfter(DaprAutoConfiguration::class)
class ApplicationAfterDaprAutoConfiguration {

    @Bean
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


    @Bean
    fun daprLocalizationListener(clusterEventPublisher: IClusterEventPublisher): LocalLocalizationEventListener {
        return LocalLocalizationEventListener(clusterEventPublisher)
    }

    @Bean
    fun daprGroup(): GroupedOpenApi {
        return GroupedOpenApi.builder()
            .group("Dapr-SDK")
            .packagesToScan("io.dapr")
            .build()
    }

    @Bean
    fun daprApplicationGroup(): GroupedOpenApi {
        return GroupedOpenApi.builder()
            .group("Dapr-Application")
            .addOpenApiMethodFilter { method ->
                method.annotations.any { it.annotationClass == Topic::class.java }
            }
            .build()
    }

    class DaprBootPrinter : IBootPrinter {
        override fun appendBootMessages(messageBuilder: StringBuilder) {
            messageBuilder.appendLine("dapr: ${io.dapr.utils.Version.getSdkVersion()}")
        }
    }

    @Bean
     fun daprBootPrinter(): DaprBootPrinter {
        return DaprBootPrinter()
    }
}