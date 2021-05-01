/*
 * Auto-generated file. Created by MyBatis Generator
 */
package com.labijie.application.data.mapper

import java.sql.JDBCType
import org.mybatis.dynamic.sql.SqlTable

object SnowflakeSlotDynamicSqlSupport {
    object SnowflakeSlot : SqlTable("core_snowflake_slots") {
        val slotNumber = column<String>("slot_number", JDBCType.VARCHAR)

        val instance = column<String>("`instance`", JDBCType.VARCHAR)

        val addr = column<String>("addr", JDBCType.VARCHAR)

        val timeExpired = column<Long>("time_expired", JDBCType.BIGINT)
    }
}