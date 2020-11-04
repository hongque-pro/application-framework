/*
 * Auto-generated file. Created by MyBatis Generator
 */
package com.labijie.application.auth.data

data class UserRecord(
    var id: Long? = null,
    var userType: Byte? = null,
    var accessFailedCount: Int? = null,
    var concurrencyStamp: String? = null,
    var email: String? = null,
    var emailConfirmed: Boolean? = null,
    var language: String? = null,
    var lockoutEnabled: Boolean? = null,
    var lockoutEnd: Long? = null,
    var passwordHash: String? = null,
    var phoneNumber: String? = null,
    var phoneNumberConfirmed: Boolean? = null,
    var securityStamp: String? = null,
    var timeZone: String? = null,
    var twoFactorEnabled: Boolean? = null,
    var userName: String? = null,
    var approved: Boolean? = null,
    var approverId: Long? = null,
    var timeExpired: Long? = null,
    var timeLastLogin: Long? = null,
    var timeLastActivity: Long? = null,
    var timeCreated: Long? = null,
    var lastSignInIp: String? = null,
    var lastSignInPlatform: String? = null,
    var lastSignInArea: String? = null,
    var lastClientVersion: String? = null,
    var memberId: Long? = null
)