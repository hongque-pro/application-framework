package com.labijie.application.env

import com.labijie.infra.utils.logger
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent
import org.springframework.context.ApplicationListener
import org.springframework.core.env.Environment
import org.springframework.core.env.MapPropertySource

/**
 *
 * @Author: Anders Xiao
 * @Date: 2021/12/14
 * @Description:
 */
class InfraEnvironmentPreparedListener : ApplicationListener<ApplicationEnvironmentPreparedEvent> {
    override fun onApplicationEvent(p0: ApplicationEnvironmentPreparedEvent) {
        val env = p0.environment
        val defaults = defaultProperties(env)
        env.propertySources.addFirst(defaults)
    }

    private fun defaultProperties(origin: Environment): MapPropertySource {
        val configMap = mutableMapOf<String, Any>()
        return MapPropertySource("application-framework-config", configMap)
    }
}