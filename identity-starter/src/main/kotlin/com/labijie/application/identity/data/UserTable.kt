package com.labijie.application.identity.data

import java.util.Locale

/**
 * @author Anders Xiao
 * @date 2023-11-29
 */
object UserTable : IdentityLongIdTable("users") {

    val userName = varchar("user_name", 32).uniqueIndex()
    val userType = byte("user_type")
    val accessFailedCount = short("access_failed_count")
    val concurrencyStamp = varchar("concurrency_stamp", 32)
    val email = varchar("email", 64).uniqueIndex()
    val emailConfirmed = bool("email_confirmed").default(false)
    val language = varchar("language", 16).default(Locale.SIMPLIFIED_CHINESE.toString())
    val lockoutEnabled = bool("lockout_enabled").default(false)
    val lockoutEnd = long("lockout_end").default(0)
    val passwordHash = varchar("password_hash", 128)
    val phoneCountryCode = short("phone_country_code").default(86)
    val phoneNumber = varchar("phone_number", 32).uniqueIndex()
    val phoneNumberConfirmed = bool("phone_number_confirmed").default(false)
    val securityStamp = varchar("security_stamp", 32)
    val timeZone = varchar("time_zone", 32)
    val twoFactorEnabled = bool("two_factor_enabled").default(false)
    val approved = bool("approved").default(false)
    val approverId = long("approver_id").default(0)
    val timeExpired = long("time_expired").default(Long.MAX_VALUE)
    val timeLastLogin = long("time_last_login").default(0)
    val timeLastActivity = long("time_last_activity").default(0)
    val timeCreated = long("time_created").default(0)
    val lastSignInIp = varchar("last_sign_in_ip", 48).default("0.0.0.0")
    val lastSignInPlatform = varchar("last_sign_in_platform", 16).default("")
    val lastSignInArea = varchar("last_sign_in_area", 32).default("")
    val lastClientVersion = varchar("last_client_version", 32).default("")

}