package com.labijie.application.email

import com.labijie.application.email.model.TemplatedMail
import com.labijie.application.email.service.IEmailService

/**
 * @author Anders Xiao
 * @date 2025/8/2
 */

fun IEmailService.sendTemplated(
    to: String,
    templateId: String = "",
    templateParams: Map<String, String>? = null,
    subject: String? = null
) {
    TemplatedMail(
        to = to,
        templateId = templateId,
        templateParams = templateParams,
        subject = subject
    ).let {
        this.sendTemplated(it)
    }
}