package com.labijie.application.auth.model

import com.labijie.application.model.SmsCaptcha
import jakarta.validation.constraints.NotBlank
import org.hibernate.validator.constraints.Length
import org.hibernate.validator.constraints.Range

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-10-24
 */
class SetPasswordRequest {

    @get:Range(min = 1, message = "用户 id 不合法")
    var userId:Long = 0

    @get:NotBlank(message = "新密码不能为空")
    @get:Length(min=6, message = "密码不能少于 6 位")
    var password:String = ""

    @get:NotBlank(message = "验证码令牌不能空")
    var clientStamp: String = ""

    @get:NotBlank(message = "短信验证码不能为空")
    var code: String = ""
}