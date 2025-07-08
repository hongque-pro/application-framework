package com.labijie.application.dapr.configuration

import com.labijie.application.dapr.DaprDisposable
import com.labijie.infra.oauth2.resource.IResourceAuthorizationConfigurer
import io.dapr.springboot.DaprAutoConfiguration
import org.springframework.beans.factory.config.BeanDefinition
import org.springframework.boot.autoconfigure.AutoConfigureBefore
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Role

/**
 * @author Anders Xiao
 * @date 2025-05-16
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(DaprProperties::class)
@AutoConfigureBefore(DaprAutoConfiguration::class)
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
class ApplicationBeforeDaprAutoConfiguration {

    @ConditionalOnClass(name = ["com.labijie.infra.oauth2.resource.IResourceAuthorizationConfigurer"])
    @Configuration(proxyBeanMethods = false)
    protected class DaprResourceAutoConfiguration {

        @Bean
        fun daprResourceConfigurer(): IResourceAuthorizationConfigurer {
            return DaprResourceAuthorizationConfigurer()
        }
    }


    @Bean
    @ConditionalOnMissingBean(DaprDisposable::class)
    fun daprModuleInitializer(properties: DaprProperties): DaprDisposable {
        return DaprDisposable( properties)
    }

    @Bean
    @ConditionalOnMissingBean(ApplicationDaprBeanPostProcessor::class)
    fun applicationDaprBeanPostProcessor(daprProperties: DaprProperties) : ApplicationDaprBeanPostProcessor {
        return ApplicationDaprBeanPostProcessor(daprProperties);
    }

}