package com.labijie.application.dapr.configuration

/**
 * @author Anders Xiao
 * @date 2023-12-09
 */
data class SecretsStoreConfig(
    var oauth2ServerEnabled: Boolean = true,
    var resourceServerEnabled: Boolean = true,

    var oauth2Store: String = "",
    var oauth2ServerPrivateKey: String = "",
    var oauth2ServerPublicKey: String = "",
    var resourceServerPublicKey: String = ""
)