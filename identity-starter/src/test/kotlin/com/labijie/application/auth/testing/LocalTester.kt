/**
 * @author Anders Xiao
 * @date 2024-07-24
 */
package com.labijie.application.auth.testing

import com.labijie.application.getId
import com.labijie.application.identity.data.pojo.User
import com.labijie.application.identity.locale
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull


class LocalTester {

    @Test
    fun localToString() {
        val locale = Locale.SIMPLIFIED_CHINESE
        assertEquals("zh-CN", locale.getId())

        val us = Locale.US
        assertEquals("en-US", us.getId())
    }


    @Test
    fun toLocale() {
        val u = User().apply {
            this.language = "xx_xx"
        }
        val local = u.locale
        assertNull(local)


        val u2 = User().apply {
            this.language = "zh-CN"
        }
        val local2 = u2.locale
        assertNotNull(local2)

        val u3 = User().apply {
            this.language = "zh_CN"
        }
        val local3 = u3.locale
        assertNotNull(local3)
    }
}