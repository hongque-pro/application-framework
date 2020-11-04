/*
 * Auto-generated file. Created by MyBatis Generator
 */
package com.labijie.application.auth.data.mapper

import java.sql.JDBCType
import org.mybatis.dynamic.sql.SqlTable

object UserOpenIdDynamicSqlSupport {
    object UserOpenId : SqlTable("identity_openids") {
        val appId = column<String>("app_id", JDBCType.VARCHAR)

        val userId = column<Long>("user_id", JDBCType.BIGINT)

        val loginProvider = column<String>("login_provider", JDBCType.VARCHAR)

        val openId = column<String>("open_id", JDBCType.VARCHAR)
    }
}