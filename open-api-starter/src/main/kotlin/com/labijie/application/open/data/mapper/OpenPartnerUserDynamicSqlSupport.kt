/*
 * Auto-generated file. Created by MyBatis Generator
 */
package com.labijie.application.open.data.mapper

import java.sql.JDBCType
import org.mybatis.dynamic.sql.SqlTable

object OpenPartnerUserDynamicSqlSupport {
    object OpenPartnerUser : SqlTable("open_partner_user") {
        val partnerId = column<Long>("partner_id", JDBCType.BIGINT)

        val userId = column<Long>("user_id", JDBCType.BIGINT)
    }
}