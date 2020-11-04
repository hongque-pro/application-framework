/*
 * Auto-generated file. Created by MyBatis Generator
 */
package com.labijie.application.open.data.mapper

import java.sql.JDBCType
import org.mybatis.dynamic.sql.SqlTable

object OpenPartnerDynamicSqlSupport {
    object OpenPartner : SqlTable("open_partners") {
        val id = column<Long>("id", JDBCType.BIGINT)

        val name = column<String>("`name`", JDBCType.VARCHAR)

        val timeExpired = column<Long>("time_expired", JDBCType.BIGINT)

        val status = column<Byte>("`status`", JDBCType.TINYINT)

        val timeLatestPaid = column<Long>("time_latest_paid", JDBCType.BIGINT)

        val timeLatestUpdated = column<Long>("time_latest_updated", JDBCType.BIGINT)

        val phoneNumber = column<String>("phone_number", JDBCType.VARCHAR)

        val contact = column<String>("contact", JDBCType.VARCHAR)

        val email = column<String>("email", JDBCType.VARCHAR)

        val appCount = column<Short>("app_count", JDBCType.SMALLINT)
    }
}