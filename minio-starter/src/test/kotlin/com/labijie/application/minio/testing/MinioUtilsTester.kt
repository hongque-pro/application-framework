package com.labijie.application.minio.testing

import com.labijie.application.propertiesFrom
import com.labijie.appliction.minio.MinioUtils
import com.labijie.appliction.minio.configuration.MinioProperties
import okhttp3.OkHttpClient
import org.junit.jupiter.api.Assertions
import org.springframework.boot.test.web.client.TestRestTemplate
import java.net.URL
import kotlin.test.Test

class MinioUtilsTester {

    @Test
    fun testAssumeRole() {
        val minioProperties = MinioProperties(
            endpoint = URL("http://47.100.188.60:9000"),
            accessKey = "minioadmin",
            secretKey = "minioadmin"
        )

        val utils = MinioUtils("dummy-app", minioProperties, OkHttpClient())

        val result = utils.assumeRole()

        Assertions.assertNotNull(result)
    }
}