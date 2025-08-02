package com.labijie.application.email.provider

import com.labijie.application.email.model.TemplatedMail
import com.labijie.application.model.VerificationCodeType
import org.slf4j.LoggerFactory

/**
 * @author Anders Xiao
 * @date 2025/7/29
 */
object DummyEmailProvider : IEmailServiceProvider {
    override val name: String
        get() = "dummy"

    private val logger = LoggerFactory.getLogger(DummyEmailProvider::class.java)


    override fun sendTemplateMailAsync(mail: TemplatedMail) {

        val param = mail.templateParams?.map { "  ${it.key}=${it.value}" }?.joinToString(System.lineSeparator()) ?: "<null>"
        StringBuilder().apply {
            appendLine("Dummy templated mail sending:")
            appendLine("to: ${mail.to}")
            appendLine("subject: ${mail.subject ?: "<null>"}")
            appendLine("template id: ${mail.templateId}")
            appendLine("template params: ${System.lineSeparator()}$param")
            appendLine()
        }.let {
            logger.info(it.toString())
        }
    }

    override fun sendVerificationCodeAsync(
        to: String,
        code: String,
        type: VerificationCodeType
    ) {
        StringBuilder().apply {
            appendLine("Dummy mail verification code sending:")
            appendLine("to: $to")
            appendLine("code: $code")
            appendLine("type: $type")
            appendLine()
        }.let {
            logger.info(it.toString())
        }
    }
}