package com.labijie.application.auth.model

import com.labijie.application.model.SmsCaptcha
import com.labijie.application.validation.ConfigurablePattern
import jakarta.validation.constraints.NotBlank

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-10-19
 */
class ChangePhoneRequest(
    /**
     * 通过调用验证旧手机号接口产生的令牌
     */
    @get:NotBlank(message = "身份安全令牌不能为空")
    var token: String = "",
) : SmsCaptcha()
{
    @get:NotBlank(message = "手机号不能为空")
    @get:ConfigurablePattern("phone-number", message = "不是有效的手机号")
    override var phoneNumber: String
        get() = super.phoneNumber
        set(value) { super.phoneNumber = value }
}