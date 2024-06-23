package com.labijie.application.identity.data

import java.time.Duration


/**
 * @author Anders Xiao
 * @date 2023-11-29
 */
object OAuth2ClientTable : IdentityTable("oauth2_clients") {
    val clientId = varchar("client_id", 32)
    val resourceIds = varchar("resource_ids", 256)
    val clientSecret = varchar("client_secret", 256)
    val scopes = varchar("scopes", 256)
    val authorizedGrantTypes = varchar("authorized_grant_types", 256)
    val redirectUrls = varchar("redirect_url", 1024).default("")
    val authorities = varchar("authorities", 256).default("")
    val additionalInformation = varchar("additional_information", 4096).default("")
    val autoApprove = bool("auto_approve")
    val enabled = bool("enabled").default(true).index()
    val accessTokenLiveSeconds = integer("access_token_live_sec").default(Duration.ofMinutes(5).toSeconds().toInt())
    val refreshTokenLiveSeconds = integer("refresh_token_live_sec").default(Duration.ofDays(7).toSeconds().toInt())
    val authorizationCodeLiveSeconds = integer("authorization_Code_Live_sec").default(Duration.ofMinutes(5).toSeconds().toInt())
    val deviceCodeLiveSeconds = integer("device_code_live_sec").default(Duration.ofMinutes(5).toSeconds().toInt())
    val reuseRefreshTokens = bool("reuse_refresh_tokens").default(true)
    override val primaryKey: PrimaryKey
        get() = PrimaryKey(clientId)
}