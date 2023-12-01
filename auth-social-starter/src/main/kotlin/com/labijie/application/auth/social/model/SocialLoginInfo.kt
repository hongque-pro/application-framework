package com.labijie.application.auth.social.model

import jakarta.validation.constraints.NotBlank
import org.hibernate.validator.constraints.Length

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