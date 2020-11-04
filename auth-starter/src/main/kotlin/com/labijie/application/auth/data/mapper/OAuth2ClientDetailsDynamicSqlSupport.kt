/*
 * Auto-generated file. Created by MyBatis Generator
 */
package com.labijie.application.auth.data.mapper

import java.sql.JDBCType
import org.mybatis.dynamic.sql.SqlTable

object OAuth2ClientDetailsDynamicSqlSupport {
    object OAuth2ClientDetails : SqlTable("identity_oauth_client_details") {
        val clientId = column<String>("client_id", JDBCType.VARCHAR)

        val resourceIds = column<String>("resource_ids", JDBCType.VARCHAR)

        val clientSecret = column<String>("client_secret", JDBCType.VARCHAR)

        val scope = column<String>("`scope`", JDBCType.VARCHAR)

        val authorizedGrantTypes = column<String>("authorized_grant_types", JDBCType.VARCHAR)

        val webServerRedirectUri = column<String>("web_server_redirect_uri", JDBCType.VARCHAR)

        val authorities = column<String>("authorities", JDBCType.VARCHAR)

        val accessTokenValidity = column<Int>("access_token_validity", JDBCType.INTEGER)

        val refreshTokenValidity = column<Int>("refresh_token_validity", JDBCType.INTEGER)

        val additionalInformation = column<String>("additional_information", JDBCType.VARCHAR)

        val autoapprove = column<String>("autoapprove", JDBCType.VARCHAR)

        val enabled = column<Boolean>("enabled", JDBCType.BIT)
    }
}