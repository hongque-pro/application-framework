package com.labijie.application.open.model

import com.labijie.application.configuration.ValidationProperties
import com.labijie.application.validation.ConfigurablePattern
import jakarta.validation.constraints.Email
import org.hibernate.validator.constraints.Length

data class OpenPartnerContact(
    @get:Length(max=16)
    var phoneNumber: String? = null,

    @get:Length(max=6)
    var contact: String? = null,

    @get:Email
    @get:Length(max=64)
    var email: String? = null
)