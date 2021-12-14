package com.labijie.application.configuration

import org.springframework.boot.SpringApplication
import org.springframework.boot.env.EnvironmentPostProcessor
import org.springframework.core.env.ConfigurableEnvironment
import org.springframework.core.env.MapPropertySource

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-12-10
 */
class DefaultConfigurationProcessor : EnvironmentPostProcessor {
    override fun postProcessEnvironment(environment: ConfigurableEnvironment, application: SpringApplication) {
        val config:Map<String, Any> = mapOf(
                "spring.cloud.httpclientfactories.apache.enabled" to false,
                "feign.okhttp.enabled" to true,
                 "feign.httpclient.enabled" to false
            )
        val propertySource = MapPropertySource(
            "framework-default-configuration", config
        )
        environment.propertySources.addFirst(propertySource)
    }
}