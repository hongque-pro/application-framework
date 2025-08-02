package com.labijie.application.identity.model

import com.labijie.application.validation.Username
import jakarta.validation.constraints.NotBlank
import org.hibernate.validator.constraints.Length

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-12-11
 */
class SocialRegisterInfo {
    @get: Length(min=3, max = 16)
    @get: Username
    var username: String? = null

    @get:NotBlank
    var password: String = ""

    @get:NotBlank
    @get:Length(max = 32)
    var provider: String = ""

    var dialingCode: Short = 86

    var phoneNumber: String = ""

    var email: String = ""

    @get:NotBlank
    var code: String = ""

    /**
     * 用于解密手机号的 IV （如果为空，表示是明文手机号）
     */
    var iv: String? = null

    var addition: MutableMap<String, String> = mutableMapOf()
}