package com.labijie.application.dapr.components

import com.labijie.application.dapr.configuration.DaprProperties
import com.labijie.infra.oauth2.component.IOAuth2ServerSecretsStore
import com.labijie.infra.utils.ifNullOrBlank
import io.dapr.client.DaprClient


/**
 * @author Anders Xiao
 * @date 2023-12-09
 */
class DaprOAuth2ServerSecretsStore(daprClient: DaprClient, private val daprProperties: DaprProperties) :
    DaprSecretsStoreBase(daprClient), IOAuth2ServerSecretsStore {
    override fun getRsaPrivateKey(): String {
        val storeName = daprProperties.secretsStore.oauth2Store.ifNullOrBlank { daprProperties.defaultSecretsStoreName }
        val key = daprProperties.secretsStore.oauth2ServerPrivateKey

        return getSecret(storeName, key)
    }

    override fun getRsaPublicKey(): String {
        val storeName = daprProperties.secretsStore.oauth2Store.ifNullOrBlank { daprProperties.defaultSecretsStoreName }
        val key = daprProperties.secretsStore.oauth2ServerPublicKey

        return getSecret(storeName, key)
    }
}