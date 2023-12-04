package com.labijie.application.model

import jakarta.validation.constraints.NotBlank


/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-10-19
 */
open class SmsAssociated {
    @get:NotBlank(message = "令牌不能为空")
    var smsToken: String = ""

    @get:NotBlank(message = "短信验证码不能为空")
    var smsCode: String = ""

    fun setSmsCode(code: String, token: String) {
        this.smsCode = code
        this.smsToken = token
    }
}