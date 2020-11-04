package com.labijie.application.model

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-11-28
 */
data class SendSmsCaptchaParam(
    var phoneNumber: String = "",
    var modifier:String = "",
    var captchaType: CaptchaType = CaptchaType.General,
    var stamp: String = ""
)