package com.labijie.application.dapr.components

import com.labijie.application.dapr.configuration.DaprProperties
import com.labijie.infra.oauth2.component.IOAuth2ServerSecretsStore
import com.labijie.infra.oauth2.configuration.OAuth2ServerProperties
import com.labijie.infra.utils.ifNullOrBlank
import io.dapr.client.DaprClient


/**
 * @author Anders Xiao
 * @date 2023-12-09
 */
class DaprOAuth2ServerSecretsStore(daprClient: DaprClient, private val daprProperties: DaprProperties) :
    DaprSecretsStoreBase(daprClient), IOAuth2ServerSecretsStore {
    override fun getRsaPrivateKey(properties: OAuth2ServerProperties): String {
        val storeName = daprProperties.secretsStore.name
        val key = properties.token.jwt.rsa.privateKey

        return getSecret(storeName, key)
    }

    override fun getRsaPublicKey(properties: OAuth2ServerProperties): String {
        val storeName = daprProperties.secretsStore.name
        val key = properties.token.jwt.rsa.publicKey

        return getSecret(storeName, key)
    }
}