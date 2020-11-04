package com.labijie.application.model

/**
 *
 * @author lishiwen
 * @date 20-5-15
 * @since JDK1.8
 */
data class SendSmsTemplateParam(
    var phoneNumber: String = "",
    var template: String = "",
    var templateParam: Map<String, Any?>? = null,
    var sendTime: Long = System.currentTimeMillis()
)