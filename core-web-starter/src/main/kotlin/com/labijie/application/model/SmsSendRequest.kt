package com.labijie.application.model

import com.labijie.application.validation.ConfigurablePattern
import jakarta.validation.constraints.NotBlank

/**
 * @author Anders Xiao
 * @date 2023-12-01
 */
class SmsSendRequest {
    @get:NotBlank(message = "手机号不能为空")
    @get:ConfigurablePattern("phone-number", message = "不是有效的手机号")
    var phoneNumber: String = ""
    var captcha: String = ""
    var type: SmsCodeType = SmsCodeType.General
}

class UserSmsSendRequest {
    var type: SmsCodeType = SmsCodeType.General
    var captcha: String = ""
}