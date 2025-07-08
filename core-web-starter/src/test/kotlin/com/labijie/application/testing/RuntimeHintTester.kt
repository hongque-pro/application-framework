package com.labijie.application.testing

import com.labijie.application.aot.ApplicationWebRuntimeHintsRegistrar
import org.springframework.aot.hint.RuntimeHints
import org.springframework.aot.hint.predicate.RuntimeHintsPredicates
import org.springframework.security.oauth2.server.authorization.web.NimbusJwkSetEndpointFilter
import kotlin.test.Test

/**
 * @author Anders Xiao
 * @date 2025/6/23
 */
class RuntimeHintTester {

    @Test
    fun testInfraWebRuntimeHint() {
        val hints = RuntimeHints()
        // 注册你的 hints
        ApplicationWebRuntimeHintsRegistrar().registerHints(hints, javaClass.classLoader)


        // 验证是否包含某个反射 hint
        val predicate = RuntimeHintsPredicates.reflection().onType(NimbusJwkSetEndpointFilter::class.java)
        assert(predicate.test(hints)) {
            "Expected NimbusJwkSetEndpointFilter to be registered for reflection, but it was not."
        }
    }
}