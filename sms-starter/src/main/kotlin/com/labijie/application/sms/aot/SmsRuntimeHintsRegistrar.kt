package com.labijie.application.sms.aot

import com.labijie.application.aot.registerPackageForJackson
import com.labijie.application.sms.model.SmsVerificationCodeSendRequest
import org.springframework.aot.hint.RuntimeHints
import org.springframework.aot.hint.RuntimeHintsRegistrar

/**
 * @author Anders Xiao
 * @date 2025/7/30
 */
class SmsRuntimeHintsRegistrar : RuntimeHintsRegistrar {
    override fun registerHints(hints: RuntimeHints, classLoader: ClassLoader?) {
        hints.reflection().registerPackageForJackson(SmsVerificationCodeSendRequest::class.java)
    }
}