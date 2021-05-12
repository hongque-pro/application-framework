package com.labijie.application.minio.testing

import com.labijie.appliction.minio.MinioUtils
import com.labijie.appliction.minio.configuration.MinioProperties
import io.minio.MinioClient
import okhttp3.OkHttpClient
import java.net.URL

object MinioTestHelper {
    private val minioProperties = MinioProperties(
        endpoint = URL("http://47.100.188.60:9001"),
        accessKey = "stsadmin",
        secretKey = "stsadmin-secret"
    )

    private const val testApplicationName = "testing-app"


    fun createMinioClient(): MinioClient {
        return MinioClient.builder()
            .endpoint(minioProperties.endpoint)
            .credentials(minioProperties.accessKey, minioProperties.secretKey).build()
    }

    fun createMinioUtils(): MinioUtils{
        return MinioUtils(testApplicationName, minioProperties, OkHttpClient())
    }
}