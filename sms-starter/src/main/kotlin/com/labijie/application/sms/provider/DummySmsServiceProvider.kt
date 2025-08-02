package com.labijie.application.sms.provider

import com.labijie.application.model.VerificationCodeType
import com.labijie.application.sms.model.TemplatedMessage
import org.slf4j.LoggerFactory

/**
 * @author Anders Xiao
 * @date 2025/7/30
 */
object DummySmsServiceProvider : ISmsServiceProvider {
    override val name: String
        get() = "dummy"

    override fun sendVerificationCodeAsync(
        dialingCode: Short,
        phoneNumber: String,
        code: String,
        type: VerificationCodeType
    ) {
        StringBuilder().apply {
            appendLine("Dummy sms verification code sent:")
            appendLine("to: ${dialingCode}${phoneNumber}")
            appendLine("code: $code")
            appendLine("type: $type")
            appendLine()
        }.let {
            logger.info(it.toString())
        }
    }

    override fun sendTemplatedAsync(message: TemplatedMessage) {
        val param = message.templateParams?.map { "  ${it.key}=${it.value}" }?.joinToString(System.lineSeparator()) ?: "<null>"

        StringBuilder().apply {
            appendLine("Dummy sms templated message sent:")
            appendLine("to: ${message.dialingCode}${message.phoneNumber}")
            appendLine("template id: ${message.templateId}")
            appendLine("template params: $param")
            appendLine()
        }.let {
            logger.info(it.toString())
        }
    }

    private val logger = LoggerFactory.getLogger(DummySmsServiceProvider::class.java)


}