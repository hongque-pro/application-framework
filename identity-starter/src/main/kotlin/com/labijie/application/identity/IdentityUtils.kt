package com.labijie.application.identity

import com.labijie.infra.utils.ShortId
import java.time.ZoneOffset
import com.labijie.application.identity.data.UserRecord as User

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
                   phoneNumber: String,
                   passwordHash: String,
                   userType: Byte): User {
        return User().apply {
            this.id = id
            this.userName = userName
            this.lockoutEnd = System.currentTimeMillis()
            this.lockoutEnabled = false
            this.language = "zh-CN"
            this.passwordHash = passwordHash
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
            this.phoneNumber = phoneNumber
            this.phoneNumberConfirmed = true
            this.securityStamp = ShortId.newId()
            this.approved = true
            this.approverId = 0
            this.memberId = 0
        }
    }
}


val User.isNullEmail
    get() = this.email?.endsWith("@null.null") ?: true