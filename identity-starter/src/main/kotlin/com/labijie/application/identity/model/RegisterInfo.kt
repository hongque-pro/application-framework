package com.labijie.application.identity.model

import com.labijie.application.configuration.ValidationProperties
import com.labijie.application.model.SmsAssociated
import com.labijie.application.validation.ConfigurablePattern
import jakarta.validation.constraints.NotBlank
import org.hibernate.validator.constraints.Length

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-09-11
 */
data class RegisterInfo(
    @get:Length(min=3, max = 16, message =  "用户名长度为 3-16 位")
    @get: ConfigurablePattern(ValidationProperties.USER_NAME, message = "用户名为 3-16 位，必须以字母开头，只能包含字母、数字、下划线、减号，且符号不能连续出现")
    var username: String?,

    @get: NotBlank(message = "手机号不能为空")
    @get: ConfigurablePattern(ValidationProperties.PHONE_NUMBER, message="不是有效的电话号码")
    var phoneNumber: String,

    @get:Length(min=6, message = "密码不能少于 6 位")
    @get: NotBlank(message = "密码不能为空")
    var password: String,

    var phoneAreaCode: String = "",


    var addition: String? = null,
) : SmsAssociated()