package com.labijie.application.dapr.components

import com.labijie.application.dapr.configuration.DaprProperties
import com.labijie.infra.oauth2.resource.component.IResourceServerSecretsStore
import com.labijie.infra.oauth2.resource.configuration.ResourceServerProperties
import com.labijie.infra.utils.ifNullOrBlank
import io.dapr.client.DaprClient

/**
 * @author Anders Xiao
 * @date 2023-12-09
 */
class DaprResourceServerSecretsStore(daprClient: DaprClient, private val daprProperties: DaprProperties) : DaprSecretsStoreBase(daprClient), IResourceServerSecretsStore {
    override fun getRsaPublicKey(properties: ResourceServerProperties): String {
        val storeName = daprProperties.secretsStore.name
        val key = properties.jwt.rsaPubKey

        return getSecret(storeName, key)
    }
}