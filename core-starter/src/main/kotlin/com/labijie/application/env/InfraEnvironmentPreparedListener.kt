package com.labijie.application.env

import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent
import org.springframework.context.ApplicationListener
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.core.env.MapPropertySource
import java.util.*

/**
 *
 * @Author: Anders Xiao
 * @Date: 2021/12/14
 * @Description:
 */
class InfraEnvironmentPreparedListener : ApplicationListener<ApplicationEnvironmentPreparedEvent> {
    override fun onApplicationEvent(p0: ApplicationEnvironmentPreparedEvent) {
        val env = p0.environment
        val defaults = defaultProperties()
        env.propertySources.addFirst(defaults)
    }

    private fun defaultProperties(): MapPropertySource {
        LocaleContextHolder.setDefaultLocale(Locale.SIMPLIFIED_CHINESE)
        val configMap = mutableMapOf<String, Any>()
        return MapPropertySource("application-framework-config", configMap)
    }
}