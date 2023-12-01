package com.labijie.application.component

import com.labijie.application.model.SendSmsCaptchaParam

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-09-09
 */
interface IMessageService {
    fun sendSmsCaptcha(param: SendSmsCaptchaParam)
    fun verifySmsCaptcha(phoneNumber:String, code: String, stamp: String, throwIfMissMatched:Boolean = false): Boolean
}