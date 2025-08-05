package com.labijie.application.identity

import com.labijie.application.getId
import com.labijie.application.identity.data.pojo.User
import com.labijie.application.identity.model.UserIdentifierType
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
    fun createUser(
        id: Long,
        userName: String? = null,
        userType: Byte = 0,
        locale: Locale? = null
    ): User {

        val username: String = if (userName.isNullOrBlank()) id.toString() else userName

        return User().apply {
            this.id = id
            this.userName = username
            this.lockoutEnd = System.currentTimeMillis()
            this.lockoutEnabled = false
            this.language = (locale ?: Locale.US).getId()
            this.accessFailedCount = 0
            this.concurrencyStamp = ShortId.newId()
            this.email = "${id}@null.null"
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
            this.phoneNumber = "N_${id}"
            this.phoneCountryCode = 0
            this.fullPhoneNumber = "0N${id}"
            this.phoneNumberConfirmed = false
            this.securityStamp = UUID.randomUUID().toString().replace("-", "")
            this.approved = true
            this.approverId = 0
        }
    }
}

val User.isNullUserName
    get() = this.userName.isBlank() || this.userName == this.id.toString() || userName.substring(0, 1)
        .toIntOrNull() != null

val User.isNullEmail
    get() = this.email.endsWith("@null.null")

val User.isNullPhoneNumber
    get() = this.phoneCountryCode == 0.toShort() || this.phoneNumber.startsWith("N_")

fun User.isEnabled(): Boolean = !this.lockoutEnabled || this.lockoutEnd < System.currentTimeMillis()

val User.locale: Locale?
    get() = try {
        LocaleUtils.toLocale(this.language)
    } catch (e: IllegalArgumentException) {
        null
    }

/**
 * set phone/email empty if that is a placeholder
 */
fun User.normalizedIdentifier() {
    if (isNullUserName) {
        this.userName = ""
    }
    if (isNullPhoneNumber) {
        this.phoneNumber = ""
        this.fullPhoneNumber = ""
        this.phoneCountryCode = 0
    }
    if (isNullEmail) {
        this.email = ""
    }
}

fun User.getIdentityType(identifier: String): UserIdentifierType {
    val type = when (identifier) {
        fullPhoneNumber -> UserIdentifierType.Phone
        email -> UserIdentifierType.Email
        userName -> UserIdentifierType.UserName
        else -> UserIdentifierType.Unknown
    }
    return type
}

fun User.setPhoneNumber(countryCode: Short, number: String) {
    this.phoneCountryCode = countryCode
    this.phoneNumber = number
    this.fullPhoneNumber = "${countryCode}${phoneNumber}"
}