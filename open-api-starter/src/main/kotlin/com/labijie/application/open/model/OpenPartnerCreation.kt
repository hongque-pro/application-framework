package com.labijie.application.open.model

import com.labijie.application.configuration.ValidationConfiguration
import com.labijie.application.validation.ConfigurablePattern
import org.hibernate.validator.constraints.Length
import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank

data class OpenPartnerCreation(
    @get:NotBlank
    @get:Length(max = 64)
    var name: String = "",

    @get:Length(max=16)
    @get:ConfigurablePattern(ValidationConfiguration.PHONE_NUMBER)
    var phoneNumber: String? = null,

    @get:Length(max=6)
    var contact: String? = null,

    @get:Email
    @get:Length(max=64)
    var email: String? = null,

    var timeExpired: Long = Long.MAX_VALUE
)