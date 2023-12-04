package com.labijie.application.core.testing

import com.labijie.application.crypto.HashUtils
import com.labijie.infra.utils.logger
import org.junit.jupiter.api.Assertions
import java.util.*
import kotlin.test.Test

class HashUtilsTester {

    @Test
    fun testSha256(){
        val data = mapOf(
            "a" to "dfsdfsdf",
            "b" to "fdsfsdfsd3232"
        )

        val data2 = mapOf(
            "a" to "1",
            "b" to "2"
        )

        val key = UUID.randomUUID().toString().replace("-", "")
        val hashed = HashUtils.signHmacSha256(data, key)
        logger.info("HmacSha256 hashed: $hashed (key: $key)")

        var valid = HashUtils.verifyHmacSha256(data, hashed, key)
        Assertions.assertTrue(valid)

        valid = HashUtils.verifyHmacSha256(data2, hashed, key)
        Assertions.assertFalse(valid)
    }
}