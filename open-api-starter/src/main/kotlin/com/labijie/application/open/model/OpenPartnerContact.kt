package com.labijie.application.open.model

import com.labijie.application.configuration.ValidationConfiguration
import com.labijie.application.validation.ConfigurablePattern
import org.hibernate.validator.constraints.Length
import javax.validation.constraints.Email

data class OpenPartnerContact(
    @Length(max=16)
    @ConfigurablePattern(ValidationConfiguration.PHONE_NUMBER)
    var phoneNumber: String? = null,

    @Length(max=6)
    var contact: String? = null,

    @Email
    @Length(max=64)
    var email: String? = null
)