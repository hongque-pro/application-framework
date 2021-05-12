package com.labijie.application.minio.testing

import com.labijie.application.propertiesFrom
import com.labijie.appliction.minio.MinioUtils
import com.labijie.appliction.minio.configuration.MinioProperties
import com.labijie.infra.json.JacksonHelper
import okhttp3.OkHttpClient
import org.junit.jupiter.api.Assertions
import org.springframework.boot.test.web.client.TestRestTemplate
import java.lang.Exception
import java.net.URL
import java.security.ProviderException
import kotlin.test.Test

class MinioUtilsTester {

    @Test
    fun testAssumeRole() {
        try {
            val minioProperties = MinioProperties(
                endpoint = URL("http://47.100.188.60:9000"),
                accessKey = "stsadmin",
                secretKey = "stsadmin-secret"
            )

            val utils = MinioUtils("dummy-app", minioProperties, OkHttpClient())

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