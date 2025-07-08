package com.labijie.application.testing

import com.labijie.application.captcha.SpecCaptcha
import java.io.ByteArrayOutputStream
import kotlin.test.Test
import kotlin.test.assertNotNull

/**
 * @author Anders Xiao
 * @date 2025/7/4
 */
class CaptchaTester {
    @Test
    fun testBase64() {
        val w = 128
        val h = 32
        val l = 4
        val capture = SpecCaptcha(w, h, l)

        assertNotNull(capture.mimeType)
        assert(capture.mimeType.startsWith("image/"))

        val image = capture.toBase64()
        assert(image.length > 100)
    }


    @Test
    fun testStream() {
        val w = 128
        val h = 32
        val l = 4
        val capture = SpecCaptcha(w, h, l)

        assertNotNull(capture.mimeType)
        assert(capture.mimeType.startsWith("image/"))

        ByteArrayOutputStream().use {
            capture.out(it)
            it.flush()

            assert(it.size() > 0)
        }

    }

}