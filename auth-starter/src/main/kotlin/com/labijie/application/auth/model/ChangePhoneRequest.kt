package com.labijie.application.auth.model

import com.labijie.application.configuration.ValidationProperties
import com.labijie.application.model.SmsAssociated
import com.labijie.application.validation.ConfigurablePattern
import jakarta.validation.constraints.NotBlank

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-10-19
 */
class ChangePhoneRequest : SmsAssociated() {
    @get:NotBlank
    @get:ConfigurablePattern(ValidationProperties.PHONE_NUMBER)
    var phoneNumber: String = ""


    @get:NotBlank
    var token: String = ""
}