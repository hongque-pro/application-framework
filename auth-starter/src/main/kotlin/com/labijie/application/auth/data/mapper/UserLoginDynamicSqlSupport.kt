/*
 * Auto-generated file. Created by MyBatis Generator
 */
package com.labijie.application.auth.data.mapper

import java.sql.JDBCType
import org.mybatis.dynamic.sql.SqlTable

object UserLoginDynamicSqlSupport {
    object UserLogin : SqlTable("identity_user_logins") {
        val loginProvider = column<String>("login_provider", JDBCType.VARCHAR)

        val providerKey = column<String>("provider_key", JDBCType.VARCHAR)

        val userId = column<Long>("user_id", JDBCType.BIGINT)

        val providerDisplayName = column<String>("provider_display_name", JDBCType.LONGVARCHAR)
    }
}