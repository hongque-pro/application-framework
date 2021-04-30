/*
 * Auto-generated file. Created by MyBatis Generator
 */
package com.labijie.application.identity.data.mapper

import java.sql.JDBCType
import org.mybatis.dynamic.sql.SqlTable

object RoleClaimDynamicSqlSupport {
    object RoleClaim : SqlTable("identity_role_claims") {
        val id = column<Long>("id", JDBCType.BIGINT)

        val claimType = column<String>("claim_type", JDBCType.VARCHAR)

        val claimValue = column<String>("claim_value", JDBCType.VARCHAR)

        val roleId = column<Long>("role_id", JDBCType.BIGINT)
    }
}