/*
 * Auto-generated file. Created by MyBatis Generator
 */
package com.labijie.application.identity.data.mapper

import java.sql.JDBCType
import org.mybatis.dynamic.sql.SqlTable

object UserRoleDynamicSqlSupport {
    object UserRole : SqlTable("identity_user_roles") {
        val userId = column<Long>("user_id", JDBCType.BIGINT)

        val roleId = column<Long>("role_id", JDBCType.BIGINT)
    }
}