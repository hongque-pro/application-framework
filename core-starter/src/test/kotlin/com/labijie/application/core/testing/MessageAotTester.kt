package com.labijie.application.core.testing

import com.labijie.application.aot.ApplicationCoreRuntimeHintsRegistrar
import org.springframework.aot.hint.RuntimeHints
import org.springframework.aot.hint.predicate.RuntimeHintsPredicates
import kotlin.test.Test

/**
 * @author Anders Xiao
 * @date 2025/6/23
 */
class MessageAotTester {

    @Test
    fun testMessageSourceHints() {
        val hints = RuntimeHints()
        ApplicationCoreRuntimeHintsRegistrar().registerHints(hints, ApplicationCoreRuntimeHintsRegistrar::class.java.classLoader)

        assert(RuntimeHintsPredicates.resource().forResource("com/labijie/application/messages.properties").test(hints))
    }
}