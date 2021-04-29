/*
 * Auto-generated file. Created by MyBatis Generator
 */
package com.labijie.application.identity.data

data class OAuth2ClientDetailsRecord(
    var clientId: String? = null,
    var resourceIds: String? = null,
    var clientSecret: String? = null,
    var scope: String? = null,
    var authorizedGrantTypes: String? = null,
    var webServerRedirectUri: String? = null,
    var authorities: String? = null,
    var accessTokenValidity: Int? = null,
    var refreshTokenValidity: Int? = null,
    var additionalInformation: String? = null,
    var autoapprove: String? = null,
    var enabled: Boolean? = null
)