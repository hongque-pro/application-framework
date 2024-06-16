package com.labijie.application.identity.model

import com.labijie.application.configuration.ValidationProperties
import com.labijie.application.model.SmsAssociated
import com.labijie.application.validation.ConfigurablePattern
import com.labijie.application.validation.Username
import jakarta.validation.constraints.NotBlank
import org.hibernate.validator.constraints.Length

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-09-11
 */
data class RegisterInfo(
    @get:Length(min=3, max = 16)
    @get: Username
    var username: String? = null,

    var phoneNumber: String = "",

    @get:Length(min=6)
    @get: NotBlank
    var password: String = "",

    var dialingCode: Short? = null,

    var addition: MutableMap<String, String> = mutableMapOf(),

    var email: String = "",
) : SmsAssociated()