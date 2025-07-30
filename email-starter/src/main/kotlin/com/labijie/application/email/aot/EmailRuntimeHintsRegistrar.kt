package com.labijie.application.email.aot

import com.labijie.application.aot.registerForJackson
import com.labijie.application.email.model.EmailVerificationSendRequest
import com.labijie.application.email.model.TemplatedMail
import org.springframework.aot.hint.RuntimeHints
import org.springframework.aot.hint.RuntimeHintsRegistrar

/**
 * @author Anders Xiao
 * @date 2025/7/29
 */
class EmailRuntimeHintsRegistrar : RuntimeHintsRegistrar {
    override fun registerHints(hints: RuntimeHints, classLoader: ClassLoader?) {
        hints.reflection().registerForJackson(TemplatedMail::class)
        hints.reflection().registerForJackson(EmailVerificationSendRequest::class)
    }
}