package com.labijie.application.sms.provider

import com.labijie.application.model.VerificationCodeType
import com.labijie.application.sms.model.TemplatedMessage
import java.time.Duration

/**
 * @author Anders Xiao
 * @date 2025/7/30
 */
interface ISmsServiceProvider {
    val name: String
    fun sendVerificationCodeAsync(dialingCode: Short, phoneNumber: String, code: String, expiration: Duration, type: VerificationCodeType)
    fun sendTemplatedAsync(message: TemplatedMessage)
}