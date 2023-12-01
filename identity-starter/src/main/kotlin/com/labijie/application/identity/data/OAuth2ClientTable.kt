package com.labijie.application.identity.data

import org.jetbrains.exposed.sql.Table


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
    val accessTokenLiveSeconds = integer("access_token_live_sec").default(3600)
    val refreshTokenLiveSeconds = integer("refresh_token_live_sec").default(3600 * 24)

    override val primaryKey: PrimaryKey
        get() = PrimaryKey(clientId)
}