package com.labijie.application.email.model

/**
 * @author Anders Xiao
 * @date 2025/7/29
 */
data class TemplatedMail(
    var to: String,
    var subject: String? = null,
    var templateId: String = "",
    var templateParams : Map<String, String>? = null
)