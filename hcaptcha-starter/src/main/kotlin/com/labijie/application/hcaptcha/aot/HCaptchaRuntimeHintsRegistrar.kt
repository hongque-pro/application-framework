package com.labijie.application.hcaptcha.aot

import com.labijie.application.aot.registerForJackson
import com.labijie.application.hcaptcha.HCapchaVerifyResponse
import org.springframework.aot.hint.RuntimeHints
import org.springframework.aot.hint.RuntimeHintsRegistrar

/**
 * @author Anders Xiao
 * @date 2025/7/29
 */
class HCaptchaRuntimeHintsRegistrar : RuntimeHintsRegistrar {
    override fun registerHints(hints: RuntimeHints, classLoader: ClassLoader?) {
        hints.reflection().registerForJackson(HCapchaVerifyResponse::class)
    }
}