package com.labijie.application.model

import com.labijie.application.validation.ConfigurablePattern
import jakarta.validation.constraints.NotBlank


/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-10-19
 */
open class SmsCaptcha {
    open var phoneNumber: String = ""

    @get:NotBlank(message = "验证码令牌不能空")
    var clientStamp: String = ""

    @get:NotBlank(message = "短信验证码不能为空")
    var code: String = ""
}