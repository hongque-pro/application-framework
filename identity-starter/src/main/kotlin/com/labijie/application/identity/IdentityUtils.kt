package com.labijie.application.identity

import com.labijie.application.getId
import com.labijie.application.identity.data.pojo.User
import com.labijie.infra.utils.ShortId
import org.apache.commons.lang3.LocaleUtils
import java.time.ZoneOffset
import java.util.*

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
                   userType: Byte = 0,
                   locale: Locale? = null): User {
        return User().apply {
            this.id = id
            this.userName = userName
            this.lockoutEnd = System.currentTimeMillis()
            this.lockoutEnabled = false
            this.language = (locale ?: Locale.US).getId()
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

val User.locale: Locale?
    get() = try {
        LocaleUtils.toLocale(this.language)
    }catch (e: IllegalArgumentException) {
        null
    }