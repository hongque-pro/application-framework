package com.labijie.application.identity.model

import com.labijie.application.configuration.ValidationConfiguration
import com.labijie.application.model.SmsCaptcha
import com.labijie.application.validation.ConfigurablePattern
import org.hibernate.validator.constraints.Length
import javax.validation.Valid
import javax.validation.constraints.NotBlank

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-12-11
 */
class SocialRegisterInfo {
    @get:Length(min=3, max = 16, message =  "用户名长度为 3-16 位")
    @get: ConfigurablePattern(ValidationConfiguration.USER_NAME, message = "用户名为 3-16 位，必须以字母开头，只能包含字母、数字、下划线、减号，且符号不能连续出现")
    var username: String? = null

    @get:NotBlank
    var password: String = ""

    @get:NotBlank
    @get:Length(max = 32)
    var provider: String = ""

    @get:NotBlank
    @get: ConfigurablePattern(ValidationConfiguration.PHONE_NUMBER, message="不是有效的电话号码")
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