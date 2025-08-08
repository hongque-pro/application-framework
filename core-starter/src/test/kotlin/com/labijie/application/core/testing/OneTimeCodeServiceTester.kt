package com.labijie.application.core.testing

import com.labijie.application.model.OneTimeCodeTarget
import com.labijie.application.service.impl.DefaultOnetimeCodeService
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull


class OneTimeCodeServiceTester {
    private val service = DefaultOnetimeCodeService()

    @Test
    fun testMailCode() {
        val code = service.generateMailCode("test@test.test")
        val r = service.verifyCode(
            code.code, code.stamp,
            OneTimeCodeTarget.Channel.Email,
            "test@test.test",
            false
        )
        assertEquals(true, r.success)

        val r2 = service.verifyCode(
            "111111", code.stamp,
            OneTimeCodeTarget.Channel.Email,
            "test@test.test",
            false
        )
        assertEquals(false, r2.success, "bad code matched")

        val r3 = service.verifyCode(
            code.code, code.stamp,
            OneTimeCodeTarget.Channel.Email,
            "test@test2.test",
            false
        )
        assertEquals(false, r3.success, "contract matched is wrong")
    }

    @Test
    fun testSMSCode() {
        val code = service.generatePhoneCode(86, "13888888888")
        val r = service.verifyCode(
            code.code, code.stamp,
            OneTimeCodeTarget.Channel.Phone,
            "8613888888888",
            false
        )
        assertEquals(true, r.success)

        val r2 = service.verifyCode(
            "111111", code.stamp,
            OneTimeCodeTarget.Channel.Phone,
            "8613888888888",
            false
        )
        assertEquals(false, r2.success, "bad code matched")

        val r3 = service.verifyCode(
            code.code, code.stamp,
            OneTimeCodeTarget.Channel.Phone,
            "8613888888889",
            false
        )
        assertEquals(false, r3.success, "contract matched is wrong")
    }

//    @Test
//    fun testStamp() {
//        val code = "544228"
//        val stamp = "HG5LuGNIuVN9g5zt__PVdHINLMTyrcB_YUUOHhMKF-2-7ILS6TlWpg=="
//        val r = service.verifyCode(code, stamp)
//        assertEquals(true, r.success)
//        val ss = service.decodeStamp(stamp)
//
//        assertNotNull(ss)
//    }
}