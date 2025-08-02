package com.labijie.application.sms

import com.labijie.application.sms.model.TemplatedMessage
import com.labijie.application.sms.service.ISmsService

/**
 * @author Anders Xiao
 * @date 2025/8/2
 */
fun ISmsService.sendTemplated(
    dialingCode: Short,
    phoneNumber: String,
    templateId: String,
    templateParams: Map<String, String>? = null
) {
    TemplatedMessage(
        dialingCode = dialingCode,
        phoneNumber = phoneNumber,
        templateId = templateId,
        templateParams = templateParams
    ).let {
        this.sendTemplated(it)
    }
}