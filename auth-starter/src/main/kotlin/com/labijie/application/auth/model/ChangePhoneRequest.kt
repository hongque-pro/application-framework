package com.labijie.application.auth.model

import com.labijie.application.model.SmsAssociated
import com.labijie.application.validation.ConfigurablePattern
import jakarta.validation.constraints.NotBlank

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-10-19
 */
class ChangePhoneRequest : SmsAssociated() {
    @get:NotBlank(message = "手机号不能为空")
    @get:ConfigurablePattern("phone-number", message = "不是有效的手机号")
    var phoneNumber: String = ""


    @get:NotBlank(message = "身份安全令牌不能为空")
    var token: String = ""
}