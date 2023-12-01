package com.labijie.application.model

import jakarta.validation.constraints.NotBlank

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-11-28
 */
data class SendSmsCaptchaParam(
    @get:NotBlank(message = "手机号不能为空")
    var phoneNumber: String = "",

    var captchaType: CaptchaType = CaptchaType.General,

    @get:NotBlank(message = "客户端令牌不能空")
    var clientStamp: String = ""
)