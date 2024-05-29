package com.labijie.application.component

import com.labijie.application.model.SmsCodeType

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-09-09
 */

data class SmsToken(var  token: String = "")

interface IMessageService {
    /**
     * Send a message to phone, get a security token for verify.
     */
    fun sendSmsCode(dialingCode: Short, phoneNumber: String, type: SmsCodeType) : SmsToken
    fun verifySmsCode(code: String, token: String, throwIfMissMatched:Boolean = false): Boolean
}