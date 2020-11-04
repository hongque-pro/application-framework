package com.labijie.application.open.test

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
        val key = UUID.randomUUID().toString().replace("-", "")
        val hashed = HashUtils.signHmacSha256(data, key)
        logger.info("HmacSha256 hashed: $hashed (key: $key)")

        val valid = HashUtils.verifyHmacSha256(data, hashed, key)
        Assertions.assertTrue(valid)
    }
}