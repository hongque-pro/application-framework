package com.labijie.application.identity

import com.labijie.application.identity.data.pojo.User
import com.labijie.infra.utils.ShortId
import java.time.ZoneOffset
import java.util.UUID

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-09-11
 *
 */
object IdentityUtils {

    @JvmStatic
    fun createUser(id: Long,
                   userName: String,
                   userType: Byte): User {
        return User().apply {
            this.id = id
            this.userName = userName
            this.lockoutEnd = System.currentTimeMillis()
            this.lockoutEnabled = false
            this.language = "zh-CN"
            this.accessFailedCount = 0
            this.concurrencyStamp = ShortId.newId()
            this.email = "${userName.lowercase()}@null.null"
            this.emailConfirmed = false
            this.lastClientVersion = "1.0"
            this.lastSignInArea = ""
            this.lastSignInIp = "0.0.0.0"
            this.lastSignInPlatform = "web"
            this.timeCreated = System.currentTimeMillis()
            this.timeLastActivity = System.currentTimeMillis()
            this.timeLastLogin = System.currentTimeMillis()
            this.timeExpired = Long.MAX_VALUE
            this.timeZone = ZoneOffset.ofHours(8).id
            this.twoFactorEnabled = true
            this.userType = userType
            this.phoneNumber = "N_${userName.lowercase()}"
            this.phoneNumberConfirmed = false
            this.securityStamp = UUID.randomUUID().toString().replace("-", "")
            this.approved = true
            this.approverId = 0
        }
    }
}


val User.isNullEmail
    get() = this.email.endsWith("@null.null")

val User.isNullPhoneNumber
    get() = this.phoneNumber.startsWith("N_")

fun User.isEnabled(): Boolean = !this.lockoutEnabled || this.lockoutEnd < System.currentTimeMillis()