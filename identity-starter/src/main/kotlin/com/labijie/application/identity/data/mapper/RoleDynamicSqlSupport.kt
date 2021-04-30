/*
 * Auto-generated file. Created by MyBatis Generator
 */
package com.labijie.application.identity.data.mapper

import java.sql.JDBCType
import org.mybatis.dynamic.sql.SqlTable

object RoleDynamicSqlSupport {
    object Role : SqlTable("identity_roles") {
        val id = column<Long>("id", JDBCType.BIGINT)

        val concurrencyStamp = column<String>("concurrency_stamp", JDBCType.VARCHAR)

        val name = column<String>("`name`", JDBCType.VARCHAR)
    }
}