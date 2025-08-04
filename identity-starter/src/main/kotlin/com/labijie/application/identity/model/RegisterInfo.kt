package com.labijie.application.identity.model

import com.labijie.application.validation.PhoneNumber
import com.labijie.application.validation.Username
import jakarta.validation.constraints.Email
import org.hibernate.validator.constraints.Length

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-09-11
 */

@PhoneNumber("dialingCode", "phoneNumber")
data class RegisterInfo(
    @get: Length(min=3, max = 16)
    @get: Username
    var username: String? = null,

    var dialingCode: Short? = null,

    var phoneNumber: String? = null,

    @get:Length(min=6)
    var password: String? = null,


    var addition: MutableMap<String, String>? = mutableMapOf(),

    @get:Email
    var email: String? = null,

    var language: String? = null
) {
    fun hasEmail(): Boolean {
        return !email.isNullOrBlank()
    }

    fun hasPhone(): Boolean {

        return dialingCode != null && !phoneNumber.isNullOrBlank()
    }

    fun fullPhoneNumber(): String {
        return "${dialingCode?.toString() ?: ""} ${phoneNumber ?: ""}"
    }
}