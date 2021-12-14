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
    companion object {
        const val DEFAULT_STREAM_BINDER_CONFIG_KEY = "spring.cloud.stream.default-binder"

        const val DEFAULT_STREAM_SMS_OUT_CONFIG_KEY = "spring.cloud.stream.bindings.handleSms-out-0.destination"
        const val DEFAULT_STREAM_SMS_IN_CONFIG_KEY = "spring.cloud.stream.bindings.handleSms-in-0.destination"
        const val SINK_ENABLED_CONFIG_KEY = "application.sms.async.sink-enabled"
    }

    override fun onApplicationEvent(p0: ApplicationEnvironmentPreparedEvent) {
        val env = p0.environment
        val defaults = defaultProperties(env)
        env.propertySources.addFirst(defaults)
    }

    private fun defaultProperties(origin: Environment): MapPropertySource {
        val configMap = mutableMapOf<String, Any>()
        val defaultBinder = origin.getProperty(DEFAULT_STREAM_BINDER_CONFIG_KEY)
        if (defaultBinder.isNullOrBlank()) {
            val setIn = origin.getProperty(DEFAULT_STREAM_SMS_IN_CONFIG_KEY).isNullOrBlank()
            val setOut = origin.getProperty(DEFAULT_STREAM_SMS_OUT_CONFIG_KEY).isNullOrBlank()
            val sinkEnabled = (origin.getProperty(SINK_ENABLED_CONFIG_KEY) ?: "true").toBoolean()

            configMap[DEFAULT_STREAM_BINDER_CONFIG_KEY] = "memory"
            configMap[DEFAULT_STREAM_SMS_IN_CONFIG_KEY] = "sms"
            val builder = StringBuilder()
                .appendLine("$DEFAULT_STREAM_BINDER_CONFIG_KEY not set, follow configuration auto set:")
                .appendLine("$DEFAULT_STREAM_BINDER_CONFIG_KEY=memory")

            if (setOut) {
                configMap[DEFAULT_STREAM_SMS_OUT_CONFIG_KEY] = "sms"
                builder.appendLine("$DEFAULT_STREAM_SMS_OUT_CONFIG_KEY=sms")
            }
            if (setIn && sinkEnabled) {
                configMap[DEFAULT_STREAM_SMS_OUT_CONFIG_KEY] = "sms"
                builder.appendLine("$DEFAULT_STREAM_SMS_IN_CONFIG_KEY=sms")
            }

            if (setIn || setOut) {
                builder.appendLine()
                    .appendLine("If use kafka, sms will be the topic name.")
                    .appendLine("cloud stream doc:")
                    .appendLine("https://docs.spring.io/spring-cloud-stream/docs/current/reference/html/spring-cloud-stream.html")
            }

            val msg = builder.toString()
            if (msg.isNotBlank()) {
                logger.info(msg)
            }
        }
        return MapPropertySource("application-framework-config", configMap)
    }
}