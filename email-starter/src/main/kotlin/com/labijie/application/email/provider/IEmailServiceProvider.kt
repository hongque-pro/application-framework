package com.labijie.application.email.provider

import com.labijie.application.email.model.TemplatedMail
import com.labijie.application.model.VerificationCodeType
import java.time.Duration

/**
 * @author Anders Xiao
 * @date 2025/7/29
 */
interface IEmailServiceProvider {
    val name: String

    fun sendTemplateMailAsync(mail: TemplatedMail)
    fun sendVerificationCodeAsync(to: String, code: String, codeExpiration: Duration, type: VerificationCodeType)
}