package com.labijie.application.dapr.model

import com.labijie.application.model.SmsCodeType

/**
 * @author Anders Xiao
 * @date 2023-12-04
 */
data class DaprSms(var phoneNumber: String, var captchaType: SmsCodeType, var code: String)