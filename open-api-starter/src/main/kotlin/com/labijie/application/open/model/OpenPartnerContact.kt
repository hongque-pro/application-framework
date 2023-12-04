package com.labijie.application.open.model

import com.labijie.application.configuration.ValidationProperties
import com.labijie.application.validation.ConfigurablePattern
import jakarta.validation.constraints.Email
import org.hibernate.validator.constraints.Length

data class OpenPartnerContact(
    @Length(max=16)
    @ConfigurablePattern(ValidationProperties.PHONE_NUMBER)
    var phoneNumber: String? = null,

    @Length(max=6)
    var contact: String? = null,

    @Email
    @Length(max=64)
    var email: String? = null
)