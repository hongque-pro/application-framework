package com.labijie.application.sms.provider

import com.labijie.application.model.VerificationCodeType
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

    private val logger = LoggerFactory.getLogger(DummySmsServiceProvider::class.java)


}