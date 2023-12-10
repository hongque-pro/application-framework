package com.labijie.application.dapr.configuration

import com.labijie.application.dapr.components.DaprOAuth2ServerSecretsStore
import com.labijie.application.dapr.components.DaprResourceServerSecretsStore
import com.labijie.infra.oauth2.component.IOAuth2ServerSecretsStore
import com.labijie.infra.oauth2.resource.component.IResourceServerSecretsStore
import com.labijie.infra.oauth2.resource.configuration.ResourceServerAutoConfiguration
import io.dapr.client.DaprClient
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.AutoConfigureAfter
import org.springframework.boot.autoconfigure.AutoConfigureBefore
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * @author Anders Xiao
 * @date 2023-12-09
 */
@Configuration(proxyBeanMethods = false)
@AutoConfigureBefore(
    ResourceServerAutoConfiguration::class,
    name=["com.labijie.infra.oauth2.configuration.OAuth2ServerAutoConfiguration"])
@AutoConfigureAfter(ApplicationDaprAutoConfiguration::class)
class DaprOAuthSecretsStoreAutoConfiguration {

    companion object {
        val logger by lazy { LoggerFactory.getLogger(DaprOAuthSecretsStoreAutoConfiguration::class.java) }
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnProperty(prefix = "application.dapr.secrets-store", name = ["oauth2-server-rsa-keys"], havingValue = "true", matchIfMissing = false)
    @ConditionalOnClass(name = ["com.labijie.infra.oauth2.configuration.OAuth2ServerAutoConfiguration"])
    class DaprOAuth2ServerAutoConfiguration {
        @Bean
        @ConditionalOnMissingBean(IOAuth2ServerSecretsStore::class)
        fun daprOAuth2ServerSecretsStore(daprClient: DaprClient, properties: DaprProperties) : DaprOAuth2ServerSecretsStore {
            logger.info("Apply oauth2 server rsa keys from dapr secrets store.")
            return DaprOAuth2ServerSecretsStore(daprClient, properties)
        }
    }

    @Bean
    @ConditionalOnMissingBean(IResourceServerSecretsStore::class)
    @ConditionalOnProperty(prefix = "application.dapr.secrets-store", name = ["resource-server-rsa-keys"], havingValue = "true", matchIfMissing = false)
    fun daprResourceServerSecretsStore(daprClient: DaprClient, properties: DaprProperties) : DaprResourceServerSecretsStore {
        logger.info("Apply resource server rsa public key from dapr secrets store.")
        return DaprResourceServerSecretsStore(daprClient, properties)
    }
}