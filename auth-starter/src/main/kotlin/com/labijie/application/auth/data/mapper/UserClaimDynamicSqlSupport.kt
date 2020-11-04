/*
 * Auto-generated file. Created by MyBatis Generator
 */
package com.labijie.application.auth.data.mapper

import java.sql.JDBCType
import org.mybatis.dynamic.sql.SqlTable

object UserClaimDynamicSqlSupport {
    object UserClaim : SqlTable("identity_user_claims") {
        val id = column<Long>("id", JDBCType.BIGINT)

        val claimType = column<String>("claim_type", JDBCType.VARCHAR)

        val claimValue = column<String>("claim_value", JDBCType.VARCHAR)

        val userId = column<Long>("user_id", JDBCType.BIGINT)
    }
}