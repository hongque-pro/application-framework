package com.labijie.application.dapr.components

import io.dapr.client.DaprClient
import io.dapr.client.DaprClientBuilder
import org.slf4j.LoggerFactory
import org.springframework.boot.SpringApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesBindHandlerAdvisor
import org.springframework.boot.env.EnvironmentPostProcessor
import org.springframework.core.Ordered
import org.springframework.core.env.ConfigurableEnvironment
import org.springframework.core.env.MapPropertySource
import org.springframework.core.env.PropertySource
import org.springframework.core.env.get
import org.springframework.core.io.support.PropertiesLoaderUtils

/**
 * @author Anders Xiao
 * @date 2023-12-10
 */
class DaprSecretsPostProcessor : EnvironmentPostProcessor, Ordered {

    companion object {
        private val logger by lazy {
            LoggerFactory.getLogger(DaprSecretsPostProcessor::class.java)
        }
    }

    private fun ConfigurableEnvironment.isSecretsStoreEnabled(configName: String) : Boolean {
        val key = "application.dapr.secrets-store.${configName}"
        return this[key]?.toBooleanStrictOrNull() ?: false
    }

    override fun postProcessEnvironment(environment: ConfigurableEnvironment?, application: SpringApplication?) {

        if(environment == null) {
            return
        }

        val dbPasswordEnabled = environment.isSecretsStoreEnabled("datasource-password")
        val desEnabled = environment.isSecretsStoreEnabled("application-des-secret")
        val defaultUserPasswordEnabled = environment.isSecretsStoreEnabled("default-user-password")

        val defaultStore = environment["application.dapr.secrets-store.name"]
        if(defaultStore.isNullOrBlank()){
            if(dbPasswordEnabled || desEnabled) {
                logger.warn("Dapr secrets store name is not configured, dapr secrets store process skipped.")
            }
            return
        }


        val properties :MutableMap<String, Any> = mutableMapOf()

        ProcessContext(defaultStore, DaprClientBuilder().build(), environment).use {

            if(dbPasswordEnabled){
                it.setPropertiesBySecretsValue("spring.datasource.password", properties)
            }
            if(desEnabled){
                it.setPropertiesBySecretsValue("application.des-secret", properties)
            }
            if(defaultUserPasswordEnabled) {
                it.setPropertiesBySecretsValue("application.default-user-creation.password", properties)
            }

        }
        val propertySource = MapPropertySource(
            "dapr-secrets-store-apply", properties
        )
        environment.propertySources.addFirst(propertySource)
    }

    override fun getOrder(): Int {
        return Int.MAX_VALUE
    }

    class ProcessContext(
        private val secretsStoreName: String,
        val client:DaprClient,
        private val environment: ConfigurableEnvironment): DaprSecretsStoreBase(client), AutoCloseable {

        fun setPropertiesBySecretsValue(configName: String, properties: MutableMap<String, Any>) : Boolean {
            if(configName.isBlank()){
                return false
            }
            val secret = this.environment[configName]
            if(secret.isNullOrBlank()){
                logger.warn("Apply dapr secrets store: property named '${configName}' not found.")
                return false
            }
            properties[configName] = getSecret(secretsStoreName, secret)
            return true
        }
        override fun close() {
            client.close()
        }
    }
}