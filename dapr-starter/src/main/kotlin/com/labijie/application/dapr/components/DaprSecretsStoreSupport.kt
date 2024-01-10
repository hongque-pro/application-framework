package com.labijie.application.dapr.components

import com.labijie.application.ApplicationRuntimeException
import io.dapr.client.DaprClient
import org.slf4j.LoggerFactory

/**
 * @author Anders Xiao
 * @date 2023-12-10
 */
abstract class DaprSecretsStoreSupport(protected val daprClient: DaprClient) {

    @Suppress("HasPlatformType")
    protected val logger by lazy {
        LoggerFactory.getLogger(this::class.java)
    }

    protected fun getSecret(storeName: String, keyName: String): String {
        if (storeName.isBlank()) {
            throw ApplicationRuntimeException("Dapr secrets store name can not ne blank.")
        }

        val secret = daprClient.getSecret(storeName, keyName).block()
            ?: throw ApplicationRuntimeException("Unable to find dapr secret store '${keyName}'.")
        val value = secret.values.firstOrNull()
        if (value.isNullOrBlank()) {
            throw ApplicationRuntimeException("Unable to get dapr secret named '${keyName}' from store: '${storeName}'.")
        }
        return value
    }
}