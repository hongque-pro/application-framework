package com.labijie.application.auth.aot

import com.labijie.application.aot.registerPackageForJackson
import com.labijie.application.auth.controller.AccountSecurityController
import com.labijie.application.auth.model.UserVerifyResult
import org.springframework.aot.hint.RuntimeHints
import org.springframework.aot.hint.RuntimeHintsRegistrar

/**
 * @author Anders Xiao
 * @date 2025/7/30
 */
class AuthServerRuntimeHintsRegistrar : RuntimeHintsRegistrar {
    override fun registerHints(hints: RuntimeHints, classLoader: ClassLoader?) {
        hints.reflection().registerType(AccountSecurityController::class.java)


        hints.reflection().registerPackageForJackson(UserVerifyResult::class.java)
    }
}