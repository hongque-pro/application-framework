/**
 * @author Anders Xiao
 * @date 2025-09-09
 */
package com.labijie.application.web

import org.springframework.boot.SpringApplication
import org.springframework.boot.env.EnvironmentPostProcessor
import org.springframework.core.env.ConfigurableEnvironment
import org.springframework.core.env.MapPropertySource

class WebDefaultConfigurationProcessor : EnvironmentPostProcessor {

    companion object {
        const val SPRINGDOC_SHOW_OAUTH2_ENDPOINTS = "springdoc.show-oauth2-endpoints"
    }
    override fun postProcessEnvironment(environment: ConfigurableEnvironment, application: SpringApplication) {
        val configMap: MutableMap<String, Any> = mutableMapOf(
            SPRINGDOC_SHOW_OAUTH2_ENDPOINTS to false
        )

        configMap.putIfAbsent("server.forward-headers-strategy", "framework")
        configMap.putIfAbsent("server.compression.enabled", "true")
        configMap.putIfAbsent(
            "server.compression.mime-types",
            "application/json,application/xml,text/html,text/xml,text/plain,text/css,text/javascript,application/javascript"
        )
        configMap.putIfAbsent("server.http2.enabled", "true")
        val source = MapPropertySource("application-framework-defaults", configMap)
        environment.propertySources.addLast(source)
    }
}