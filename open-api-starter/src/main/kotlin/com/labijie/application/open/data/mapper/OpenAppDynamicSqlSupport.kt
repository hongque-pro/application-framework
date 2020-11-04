/*
 * Auto-generated file. Created by MyBatis Generator
 */
package com.labijie.application.open.data.mapper

import java.sql.JDBCType
import org.mybatis.dynamic.sql.SqlTable

object OpenAppDynamicSqlSupport {
    object OpenApp : SqlTable("open_apps") {
        val appId = column<Long>("app_id", JDBCType.BIGINT)

        val displayName = column<String>("display_name", JDBCType.VARCHAR)

        val appSecret = column<String>("app_secret", JDBCType.CHAR)

        val appType = column<Byte>("app_type", JDBCType.TINYINT)

        val signAlgorithm = column<String>("sign_algorithm", JDBCType.VARCHAR)

        val jsApiKey = column<String>("js_api_key", JDBCType.CHAR)

        val jsApiDomain = column<String>("js_api_domain", JDBCType.VARCHAR)

        val logoUrl = column<String>("logo_url", JDBCType.VARCHAR)

        val status = column<Byte>("`status`", JDBCType.TINYINT)

        val partnerId = column<Long>("partner_id", JDBCType.BIGINT)

        val timeCreated = column<Long>("time_created", JDBCType.BIGINT)

        val timeConfigUpdated = column<Long>("time_config_updated", JDBCType.BIGINT)

        val concurrencyStamp = column<String>("concurrency_stamp", JDBCType.VARCHAR)

        val configuration = column<String>("configuration", JDBCType.LONGVARCHAR)
    }
}