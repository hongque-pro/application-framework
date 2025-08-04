package com.labijie.application.dapr.configuration

import com.labijie.application.dapr.DaprDisposable
import com.labijie.infra.oauth2.resource.IResourceAuthorizationConfigurer
import io.dapr.springboot.DaprAutoConfiguration
import org.springframework.beans.factory.config.BeanDefinition
import org.springframework.boot.autoconfigure.AutoConfigureBefore
import org.springframework.boot.autoconfigure.AutoConfigureOrder
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Role
import org.springframework.core.Ordered

/**
 * @author Anders Xiao
 * @date 2025-05-16
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(DaprProperties::class)
@AutoConfigureBefore(DaprAutoConfiguration::class)
@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE)
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
        return DaprDisposable(properties)
    }


    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    @ConditionalOnMissingBean(ApplicationDaprBeanPostProcessor::class)
    fun applicationDaprBeanPostProcessor(): ApplicationDaprBeanPostProcessor {
        return ApplicationDaprBeanPostProcessor()
    }

}