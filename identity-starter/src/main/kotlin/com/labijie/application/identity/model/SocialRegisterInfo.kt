package com.labijie.application.identity.model

import com.labijie.application.configuration.ValidationProperties
import com.labijie.application.model.SmsAssociated
import com.labijie.application.validation.ConfigurablePattern
import jakarta.validation.constraints.NotBlank
import org.hibernate.validator.constraints.Length

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-12-11
 */
class SocialRegisterInfo : SmsAssociated() {
    @get:Length(min=3, max = 16, message =  "用户名长度为 3-16 位")
    @get: ConfigurablePattern(ValidationProperties.USER_NAME, message = "用户名为 3-16 位，必须以字母开头，只能包含字母、数字、下划线、减号，且符号不能连续出现")
    var username: String? = null

    @get:NotBlank
    var password: String = ""

    @get:NotBlank
    @get:Length(max = 32)
    var provider: String = ""

    @get:NotBlank
    @get: ConfigurablePattern(ValidationProperties.PHONE_NUMBER, message="不是有效的电话号码")
    @get:Length(max = 16)
    var phoneNumber: String = ""

    @get:NotBlank
    var code: String = ""

    /**
     * 用于解密手机号的 IV （如果为空，表示是明文手机号）
     */
    var iv: String? = null

    var addition: String = ""
}