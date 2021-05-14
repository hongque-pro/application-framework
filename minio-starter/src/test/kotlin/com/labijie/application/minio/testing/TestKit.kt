package com.labijie.application.minio.testing

import com.labijie.appliction.minio.MinioUtils
import com.labijie.appliction.minio.configuration.MinioProperties
import io.minio.MinioClient
import io.minio.credentials.AssumeRoleProvider
import okhttp3.OkHttpClient
import java.net.URL
import java.time.Duration

object TestKit {
    val minioProperties = MinioProperties(
        endpoint = URL("http://47.100.188.60:9000"),
        accessKey = "stsadmin",
        secretKey = "stsadmin-secret"
    )

    private val httpClient = OkHttpClient.Builder().connectTimeout(Duration.ofSeconds(1)).build()

    const val ApplicationName = "testing-app"

    fun createMinioClient(provider: AssumeRoleProvider): MinioClient {
        return MinioClient.builder()
            .httpClient(httpClient)
            .region(minioProperties.region)
            .endpoint(minioProperties.endpoint)
            .credentialsProvider(provider)
            .build()
    }

    fun createMinioClient(): MinioClient {
        return MinioClient.builder()
            .httpClient(httpClient)
            .region(minioProperties.region)
            .endpoint(minioProperties.endpoint)
            .credentials(minioProperties.accessKey, minioProperties.secretKey).build()
    }

    fun createMinioUtils(): MinioUtils {
        return MinioUtils(ApplicationName, minioProperties, httpClient)
    }
}