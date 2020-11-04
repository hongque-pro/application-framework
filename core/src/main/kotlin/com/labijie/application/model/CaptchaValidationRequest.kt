package com.labijie.application.model

import javax.validation.constraints.NotBlank

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-10-19
 */
open class CaptchaValidationRequest {
    @get:NotBlank
    var stamp:String = ""
    @get:NotBlank
    var captcha:String = ""
}