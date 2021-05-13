package com.labijie.application.minio.testing

import com.labijie.infra.json.JacksonHelper
import org.junit.jupiter.api.Assertions
import java.security.ProviderException
import kotlin.test.Test

class MinioUtilsTester {

    @Test
    fun testAssumeRole() {
        try {
            val utils = TestKit.createMinioUtils()

            val result = utils.assumeRole()

            Assertions.assertNotNull(result)
            Assertions.assertTrue(result.accessKey.isNotBlank())
            Assertions.assertTrue(result.secretKey.isNotBlank())

            println(JacksonHelper.defaultObjectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(result))
        }catch (e:ProviderException){
            e.printStackTrace()
        }
    }
}