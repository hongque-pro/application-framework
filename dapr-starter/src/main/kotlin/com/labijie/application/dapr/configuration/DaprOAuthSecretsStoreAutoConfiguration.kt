package com.labijie.application.dapr.configuration

import com.labijie.application.dapr.components.DaprOAuth2ServerSecretsStore
import com.labijie.application.dapr.components.DaprResourceServerSecretsStore
import com.labijie.infra.oauth2.component.IOAuth2ServerSecretsStore
import com.labijie.infra.oauth2.resource.component.IResourceServerSecretsStore
import com.labijie.infra.oauth2.resource.configuration.ResourceServerAutoConfiguration
import io.dapr.client.DaprClient
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

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass(name = ["com.labijie.infra.oauth2.configuration.OAuth2ServerAutoConfiguration"])
    class DaprOAuth2ServerAutoConfiguration {
        @Bean
        @ConditionalOnMissingBean(IOAuth2ServerSecretsStore::class)
        @ConditionalOnProperty(prefix = "application.dapr.secrets-store", name = ["oauth2-server-enabled"], havingValue = "true", matchIfMissing = true)
        fun daprOAuth2ServerSecretsStore(daprClient: DaprClient, properties: DaprProperties) : DaprOAuth2ServerSecretsStore {
            return DaprOAuth2ServerSecretsStore(daprClient, properties)
        }
    }

    @Bean
    @ConditionalOnMissingBean(IResourceServerSecretsStore::class)
    @ConditionalOnProperty(prefix = "application.dapr.secrets-store", name = ["resource-server-enabled"], havingValue = "true", matchIfMissing = true)
    fun daprResourceServerSecretsStore(daprClient: DaprClient, properties: DaprProperties) : DaprResourceServerSecretsStore {
        return DaprResourceServerSecretsStore(daprClient, properties)
    }

}