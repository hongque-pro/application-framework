package com.labijie.application

import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent
import org.springframework.context.ApplicationListener
import org.springframework.core.env.MapPropertySource

/**
 * @author Anders Xiao
 * @date 2025/7/29
 */
class WebEnvironmentPreparedListener : ApplicationListener<ApplicationEnvironmentPreparedEvent> {
    override fun onApplicationEvent(event: ApplicationEnvironmentPreparedEvent) {
        val env = event.environment

        val configMap = mutableMapOf<String, Any>()
        configMap.putIfAbsent("server.forward-headers-strategy", "framework")

        val source = MapPropertySource("application-web-auto-config-source", configMap)
        env.propertySources.addLast(source)
    }
}