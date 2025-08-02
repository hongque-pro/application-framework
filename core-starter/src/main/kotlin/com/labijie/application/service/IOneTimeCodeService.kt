package com.labijie.application.service

import com.labijie.application.model.OneTimeCode
import com.labijie.application.model.OneTimeCodeTarget
import com.labijie.application.model.OneTimeCodeVerifyResult

/**
 * @author Anders Xiao
 * @date 2025/7/30
 */
interface IOneTimeCodeService {
    fun decodeStamp(stamp: String): OneTimeCodeTarget

    fun verifyCode(code: String, stamp: String, channel: OneTimeCodeTarget.Channel? = null, contract: String? = null, throwIfInvalid: Boolean = true): OneTimeCodeVerifyResult

    fun generateCode(source: OneTimeCodeTarget): OneTimeCode

    fun generateMailCode(email: String): OneTimeCode {
        val source = OneTimeCodeTarget.fromEmail(email)
        return generateCode(source)
    }

    fun generatePhoneCode(dialingCode: Short, phoneNumber: String): OneTimeCode {
        val source = OneTimeCodeTarget.fromPhone("${dialingCode}${phoneNumber}")
        return generateCode(source)
    }

}
