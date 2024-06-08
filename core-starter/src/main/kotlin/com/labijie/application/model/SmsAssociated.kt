package com.labijie.application.model

import jakarta.validation.constraints.NotBlank


/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-10-19
 */
open class SmsAssociated {
    var smsToken: String = ""

    var smsCode: String = ""

    fun setSmsCode(code: String, token: String) {
        this.smsCode = code
        this.smsToken = token
    }
}