package com.labijie.appliction.minio.configuration

import com.labijie.appliction.minio.MinioObjectStorage
import com.labijie.appliction.minio.web.MinioStsController
import io.minio.GetObjectArgs
import io.minio.MinioClient
import okhttp3.OkHttpClient
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication
import org.springframework.boot.autoconfigure.jersey.JerseyProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(MinioProperties::class)
class MinioAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(MinioClient::class)
    fun minioClient(
        properties: MinioProperties,
        @Autowired(required = false)
        httpClient: OkHttpClient?
    ): MinioClient {
        return MinioClient.builder()
            .endpoint(properties.endpoint)
            .apply {
                if (httpClient != null) {
                    this.httpClient(httpClient)
                }
            }
            .credentials(properties.accessKey, properties.secretKey).build()
    }

    @Bean
    fun minioObjectStorage(
        minioClient: MinioClient,
        minioProperties: MinioProperties
    ): MinioObjectStorage {
        return MinioObjectStorage(minioProperties, minioClient)
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnProperty(prefix = "application.minio", name = ["sts-controller-enabled"], havingValue = "true", matchIfMissing = true)
    @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
    protected class MinioWebAutoConfiguration {

        @Bean
        @ConditionalOnMissingBean(MinioStsController::class)
        fun minioStsController(): MinioStsController {
            return minioStsController()
        }
    }
}