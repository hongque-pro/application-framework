package com.labijie.application.component

import com.labijie.application.model.SendSmsCaptchaParam
import com.labijie.application.model.SendSmsTemplateParam

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-09-09
 */
interface IMessageSender {
    fun sendSmsCaptcha(param: SendSmsCaptchaParam, async: Boolean = true)
    fun verifySmsCaptcha(code: String, stamp: String, modifier:String? = null, throwIfMissMatched:Boolean = false): Boolean
    fun sendSmsTemplate(param: SendSmsTemplateParam, async: Boolean = true, checkTimeout: Boolean = false)
}