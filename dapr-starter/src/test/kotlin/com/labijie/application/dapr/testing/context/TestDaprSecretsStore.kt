package com.labijie.application.dapr.testing.context

import com.labijie.application.dapr.components.DaprSecretsStoreSupport
import com.labijie.application.dapr.configuration.DaprProperties
import io.dapr.client.DaprClient

/**
 * @author Anders Xiao
 * @date 2023-12-10
 */
class TestDaprSecretsStore(daprClient: DaprClient, val daprProperties: DaprProperties) : DaprSecretsStoreSupport(daprClient) {
    fun getSecretFromDapr(storeName: String, keyName: String): String {
        return getSecret(storeName, keyName)
    }
}