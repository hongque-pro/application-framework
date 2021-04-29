/*
 * Auto-generated file. Created by MyBatis Generator
 */
package com.labijie.application.identity.data.mapper

import java.sql.JDBCType
import org.mybatis.dynamic.sql.SqlTable

object UserDynamicSqlSupport {
    object User : SqlTable("identity_users") {
        val id = column<Long>("id", JDBCType.BIGINT)

        val userType = column<Byte>("user_type", JDBCType.TINYINT)

        val accessFailedCount = column<Int>("access_failed_count", JDBCType.INTEGER)

        val concurrencyStamp = column<String>("concurrency_stamp", JDBCType.VARCHAR)

        val email = column<String>("email", JDBCType.VARCHAR)

        val emailConfirmed = column<Boolean>("email_confirmed", JDBCType.BIT)

        val language = column<String>("`language`", JDBCType.VARCHAR)

        val lockoutEnabled = column<Boolean>("lockout_enabled", JDBCType.BIT)

        val lockoutEnd = column<Long>("lockout_end", JDBCType.BIGINT)

        val passwordHash = column<String>("password_hash", JDBCType.VARCHAR)

        val phoneNumber = column<String>("phone_number", JDBCType.VARCHAR)

        val phoneNumberConfirmed = column<Boolean>("phone_number_confirmed", JDBCType.BIT)

        val securityStamp = column<String>("security_stamp", JDBCType.VARCHAR)

        val timeZone = column<String>("time_zone", JDBCType.VARCHAR)

        val twoFactorEnabled = column<Boolean>("two_factor_enabled", JDBCType.BIT)

        val userName = column<String>("user_name", JDBCType.VARCHAR)

        val approved = column<Boolean>("approved", JDBCType.BIT)

        val approverId = column<Long>("approver_id", JDBCType.BIGINT)

        val timeExpired = column<Long>("time_expired", JDBCType.BIGINT)

        val timeLastLogin = column<Long>("time_last_login", JDBCType.BIGINT)

        val timeLastActivity = column<Long>("time_last_activity", JDBCType.BIGINT)

        val timeCreated = column<Long>("time_created", JDBCType.BIGINT)

        val lastSignInIp = column<String>("last_sign_in_ip", JDBCType.VARCHAR)

        val lastSignInPlatform = column<String>("last_sign_in_platform", JDBCType.VARCHAR)

        val lastSignInArea = column<String>("last_sign_in_area", JDBCType.VARCHAR)

        val lastClientVersion = column<String>("last_client_version", JDBCType.VARCHAR)

        val memberId = column<Long>("member_id", JDBCType.BIGINT)
    }
}