package com.labijie.application.configuration

import org.springframework.boot.SpringApplication
import org.springframework.boot.env.EnvironmentPostProcessor
import org.springframework.boot.logging.LogLevel
import org.springframework.core.env.ConfigurableEnvironment
import org.springframework.core.env.MapPropertySource

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-12-10
 */
class DefaultConfigurationProcessor : EnvironmentPostProcessor {
    override fun postProcessEnvironment(environment: ConfigurableEnvironment, application: SpringApplication) {

        /**
         * logging:
         *   level:
         *     httpclient: debug
         */
        val config = mutableMapOf(
            "spring.cloud.httpclientfactories.apache.enabled" to false,
            "feign.okhttp.enabled" to false,
            "feign.httpclient.enabled" to false,
        )
        val propertySource = MapPropertySource(
            "framework-default-configuration", config.toMap()
        )


        environment.propertySources.addFirst(propertySource)
    }

//    private fun ConfigurableEnvironment.getConfig(property: String): String? {
//        this.propertySources.forEach {
//            val v = it.getProperty(property)
//            if (v != null) {
//                return v.toString()
//            }
//        }
//        return null
//    }
}