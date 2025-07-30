package com.labijie.application.email.service

import com.labijie.application.email.model.TemplatedMail
import com.labijie.application.model.VerificationCodeType
import com.labijie.application.model.VerificationToken

/**
 * @author Anders Xiao
 * @date 2025/7/29
 */
interface IEmailService {
    fun send(mail: TemplatedMail)
    fun sendVerificationCode(to: String, type: VerificationCodeType) : VerificationToken
}