package com.labijie.application.model

import com.labijie.application.configuration.ValidationProperties
import com.labijie.application.validation.ConfigurablePattern
import jakarta.validation.constraints.NotBlank

/**
 * @author Anders Xiao
 * @date 2023-12-01
 */
class SmsSendRequest {
    var dialingCode: Short = 86
    var phoneNumber: String = ""
    var captcha: String = ""
    var type: SmsCodeType = SmsCodeType.General
}

class UserSmsSendRequest {
    var type: SmsCodeType = SmsCodeType.General
    var captcha: String = ""
}