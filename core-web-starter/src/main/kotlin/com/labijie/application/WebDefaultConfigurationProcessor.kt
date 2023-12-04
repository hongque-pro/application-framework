package com.labijie.application

import org.springframework.boot.SpringApplication
import org.springframework.boot.env.EnvironmentPostProcessor
import org.springframework.core.env.ConfigurableEnvironment
import org.springframework.core.env.MapPropertySource

/**
 * @author Anders Xiao
 * @date 2023-12-02
 */
class WebDefaultConfigurationProcessor : EnvironmentPostProcessor {
    override fun postProcessEnvironment(environment: ConfigurableEnvironment, application: SpringApplication) {
        val config:Map<String, Any> = mapOf(
            "springdoc.show-oauth2-endpoints" to true
        )
        val propertySource = MapPropertySource(
            "framework-web-configuration", config
        )
        environment.propertySources.addLast(propertySource)
    }
}