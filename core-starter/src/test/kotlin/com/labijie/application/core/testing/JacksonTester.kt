package com.labijie.application.core.testing

import com.labijie.infra.json.JacksonHelper
import org.apache.commons.lang3.LocaleUtils
import org.junit.jupiter.api.Assertions
import java.util.Locale
import kotlin.test.Test

/**
 * @author Anders Xiao
 * @date 2023-12-06
 */
class JacksonTester {
    @Test
    fun localSerialize() {
        val locale = Locale.SIMPLIFIED_CHINESE
        val r = JacksonHelper.serializeAsString(Locale.SIMPLIFIED_CHINESE)
        Assertions.assertNotNull(r)

        val l = JacksonHelper.deserializeFromString(r, Locale::class)
        Assertions.assertEquals(l, locale)

        LocaleUtils.toLocale()
        val l2 = JacksonHelper.deserializeFromString("zh-CN", Locale::class)
        Assertions.assertEquals(l, locale)
    }
}