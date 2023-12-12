package com.labijie.application.dapr.configuration

/**
 * @author Anders Xiao
 * @date 2023-12-09
 */
class SecretsStoreConfig(
    var name: String = "",
    /**
     * Replace application.des-secret with the value stored in the dapr secrets store.
     */
    var applicationDesSecret:Boolean = false,
    /**
     * Replace infra.oauth2.token.jwt.rsa.private-key and infra.oauth2.token.jwt.rsa.public-key with stored in the dapr secrets store.
     */
    var oauth2ServerRsaKeys: Boolean = false,
    /**
     * Replace infra.oauth2.resource-server.jwt.rsa-pub-key with the value stored in the dapr secrets store.
     */
    var resourceServerRsaKeys: Boolean = false,
    /**
     * Replace spring.datasource.password with the value stored in the dapr secrets store.
     */
    var datasourcePassword: Boolean = false,

    /**
     * Replace application.default-user-creation.password with the value stored in the dapr secrets store.
     */
    var defaultUserPassword: Boolean = false
)