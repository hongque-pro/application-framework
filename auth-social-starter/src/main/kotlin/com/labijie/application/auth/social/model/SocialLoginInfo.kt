package com.labijie.application.auth.social.model

import org.hibernate.validator.constraints.Length
import javax.validation.constraints.NotBlank

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-12-11
 */
class SocialLoginInfo {
    @get:NotBlank
    @get: Length(max = 32)
    var provider: String = ""

    @get:NotBlank
    var code: String = ""
}