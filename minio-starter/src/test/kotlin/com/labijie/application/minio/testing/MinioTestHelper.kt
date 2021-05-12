package com.labijie.application.minio.testing

import com.labijie.appliction.minio.MinioUtils
import com.labijie.appliction.minio.configuration.MinioProperties
import io.minio.MinioClient
import okhttp3.OkHttpClient
import java.net.URL
import java.time.Duration

object MinioTestHelper {
    private val minioProperties = MinioProperties(
        endpoint = URL("http://47.100.188.60:9000"),
        accessKey = "stsadmin",
        secretKey = "stsadmin-secret"
    )

    private val httpClient =  OkHttpClient.Builder().connectTimeout(Duration.ofSeconds(1)).build()

    private const val testApplicationName = "testing-app"


    fun createMinioClient(): MinioClient {
        return MinioClient.builder()
            .httpClient(httpClient)
            .endpoint(minioProperties.endpoint)
            .credentials(minioProperties.accessKey, minioProperties.secretKey).build()
    }

    fun createMinioUtils(): MinioUtils{
        return MinioUtils(testApplicationName, minioProperties, httpClient)
    }
}