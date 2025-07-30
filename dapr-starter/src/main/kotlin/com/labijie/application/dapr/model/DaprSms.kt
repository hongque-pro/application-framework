package com.labijie.application.dapr.model

import com.labijie.application.model.VerificationCodeType

/**
 * @author Anders Xiao
 * @date 2023-12-04
 */
data class DaprSms(var dialingCode: Short, var phoneNumber: String, var captchaType: VerificationCodeType, var code: String)