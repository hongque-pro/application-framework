package com.labijie.application.sms.model

/**
 * @author Anders Xiao
 * @date 2025/7/30
 */
data class TemplatedMessage(
    var dialingCode: Short = 86,
    var phoneNumber: String = "",
    var templateId: String = "",
    var templateParams : Map<String, String>? = null
)