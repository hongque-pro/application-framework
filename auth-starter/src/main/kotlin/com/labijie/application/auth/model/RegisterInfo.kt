package com.labijie.application.auth.model

import com.labijie.application.configuration.ValidationConfiguration
import com.labijie.application.validation.ConfigurablePattern
import org.hibernate.validator.constraints.Length
import javax.validation.constraints.NotBlank

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-09-11
 */
data class RegisterInfo(

    @field:NotBlank(message = "用户名不能为空")
    @field: ConfigurablePattern(ValidationConfiguration.USER_NAME, message = "用户名为 3-16 位，必须以字母开头，只能包含字母、数字、下划线、减号，且符号不能连续出现")
    var userName: String,

    @field: NotBlank(message = "用户名不能为空")
    @field: ConfigurablePattern(ValidationConfiguration.PHONE_NUMBER, message="不是有效的电话号码")
    var phoneNumber: String,

    @field:Length(min=6, message = "密码不能少于 6 位")
    @field: NotBlank(message = "密码不能为空")
    var password: String,

    @field: NotBlank(message = "重复密码不能为空")
    var repeatPassword: String,

    @field:Length(min=6, max=6, message = "验证码不正确")
    @field: NotBlank(message = "验证码不能为空")
    var captcha:String,

    @field: NotBlank(message = "缺少验证码令牌")
    var captchaStamp:String) {
}