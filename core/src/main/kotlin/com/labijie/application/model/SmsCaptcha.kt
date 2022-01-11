package com.labijie.application.model

import javax.validation.constraints.NotBlank

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-10-19
 */
open class SmsCaptcha {
    var modifier: String? = null

    @get:NotBlank(message = "验证码令牌不能空")
    var stamp: String = ""

    @get:NotBlank(message = "短信验证码不能为空")
    var captcha: String = ""
}