package com.labijie.application.sms.service

import com.labijie.application.model.VerificationCodeType
import com.labijie.application.model.VerificationToken
import com.labijie.application.sms.model.TemplatedMessage

/**
 * @author Anders Xiao
 * @date 2025/7/30
 */
interface ISmsService {

    fun sendVerificationCode(dialingCode: Short, phoneNumber: String, type: VerificationCodeType) : VerificationToken
    fun sendTemplated(message: TemplatedMessage)
}