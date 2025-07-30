package com.labijie.application

import org.springdoc.core.utils.Constants.SPRINGDOC_SHOW_OAUTH2_ENDPOINTS
import org.springframework.boot.SpringApplication
import org.springframework.boot.env.EnvironmentPostProcessor
import org.springframework.core.env.ConfigurableEnvironment
import org.springframework.core.env.MapPropertySource
import org.springframework.core.env.get

/**
 * @author Anders Xiao
 * @date 2023-12-02
 */
class WebDefaultConfigurationProcessor : EnvironmentPostProcessor {
    override fun postProcessEnvironment(environment: ConfigurableEnvironment, application: SpringApplication) {
        val config: MutableMap<String, Any> = mutableMapOf(
            SPRINGDOC_SHOW_OAUTH2_ENDPOINTS to false
        )

        val hasForward = environment.containsProperty("server.forward-headers-strategy")

        if(!hasForward) {
            config.putIfAbsent("server.forward-headers-strategy", "framework")
        }

        val propertySource = MapPropertySource(
            "framework-web-configuration", config
        )


        environment.propertySources.addLast(propertySource)
    }
}