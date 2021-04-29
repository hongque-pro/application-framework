/*
 * Auto-generated file. Created by MyBatis Generator
 */
package com.labijie.application.identity.data.mapper

import java.sql.JDBCType
import org.mybatis.dynamic.sql.SqlTable

object UserTokenDynamicSqlSupport {
    object UserToken : SqlTable("identity_user_tokens") {
        val userId = column<Long>("user_id", JDBCType.BIGINT)

        val loginProvider = column<String>("login_provider", JDBCType.VARCHAR)

        val token = column<String>("token", JDBCType.VARCHAR)

        val name = column<String>("`name`", JDBCType.VARCHAR)
    }
}