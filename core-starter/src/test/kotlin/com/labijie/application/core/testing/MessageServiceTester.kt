package com.labijie.application.core.testing

import com.labijie.application.component.impl.NoneMessageService
import com.labijie.application.configuration.ApplicationCoreProperties
import com.labijie.application.configuration.SmsBaseProperties
import com.labijie.application.model.SmsCodeType
import org.junit.jupiter.api.Assertions
import kotlin.test.Test

/**
 * @author Anders Xiao
 * @date 2023-12-01
 */
class MessageServiceTester {

    class TestMessageService : NoneMessageService() {
        override val smsBaseSettings: SmsBaseProperties
            get() = SmsBaseProperties()

        override val applicationProperties: ApplicationCoreProperties
            get() = ApplicationCoreProperties()
    }

    private val messageService = TestMessageService()

    @Test
    fun correctCodeAndVerify() {
        val token = messageService.sendSmsCode("13000000000", SmsCodeType.Register)
        val code = messageService.latestCode

        val ok = messageService.verifySmsCode(code, token.token)
        Assertions.assertTrue(ok)


    }

    @Test
    fun incorrectCodeAndVerify() {
        val token = messageService.sendSmsCode("13000000000", SmsCodeType.Register)

        val failed = messageService.verifySmsCode("32323", token.token)
        Assertions.assertFalse(failed)
    }
}