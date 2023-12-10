package com.labijie.application.dapr.components

import com.labijie.infra.utils.ifNullOrBlank
import com.labijie.infra.utils.logger
import io.dapr.client.DaprClient
import io.dapr.client.DaprClientBuilder
import org.springframework.boot.SpringApplication
import org.springframework.boot.env.EnvironmentPostProcessor
import org.springframework.core.Ordered
import org.springframework.core.env.ConfigurableEnvironment
import org.springframework.core.env.MapPropertySource
import org.springframework.core.env.get

/**
 * @author Anders Xiao
 * @date 2023-12-10
 */
class DaprDbEnvironmentPostProcessor() : EnvironmentPostProcessor, Ordered {
    class DbPasswordStore(client: DaprClient) : DaprSecretsStoreBase(client) {
        fun getDbPassword(storeName: String, secretName: String) {
            getSecret(storeName, secretName)
        }

    }

    override fun postProcessEnvironment(environment: ConfigurableEnvironment?, application: SpringApplication?) {

        val key = environment?.get("application.dapr.secrets-store.db-password") ?: ""

        if(key.isBlank()) {
            return
        }

        val defaultStore = environment?.get("application.dapr.default-secrets-store")
        var dbStore = environment?.get("application.dapr.secrets-store.db-password-store")


        dbStore = dbStore.ifNullOrBlank { defaultStore ?: "" }
        if(dbStore.isBlank()) {
            logger.warn("Dapr db password secrets store was not configured.")
            return
        }



        val password = DaprClientBuilder().build().use {
            val store = DbPasswordStore(it)
            store.getDbPassword(dbStore, key)
        }

        val config:Map<String, Any> = mapOf(
            "spring.datasource.password" to password
        )
        val propertySource = MapPropertySource(
            "dapr-secrets-configuration", config
        )

        environment?.propertySources?.addFirst(propertySource)
    }

    override fun getOrder(): Int {
        return Int.MAX_VALUE
    }

}