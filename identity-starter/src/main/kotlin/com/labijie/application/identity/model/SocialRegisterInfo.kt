package com.labijie.application.identity.model

import com.labijie.application.model.SmsCaptcha
import org.hibernate.validator.constraints.Length
import javax.validation.Valid
import javax.validation.constraints.NotBlank

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-12-11
 */
class SocialRegisterInfo {
    @get:Length(max = 32)
    var username: String? = null

    @get:NotBlank
    var password: String = ""

    @get:NotBlank
    @get:Length(max = 32)
    var provider: String = ""

    @get:NotBlank
    @get:Length(max = 16)
    var phoneNumber: String = ""

    @get:NotBlank
    var code: String = ""

    @Valid
    var captcha: SmsCaptcha? = null

    /**
     * 用于解密手机号的 IV （如果为空，表示是明文手机号）
     */
    var iv: String? = null

    var addition: String = ""
}