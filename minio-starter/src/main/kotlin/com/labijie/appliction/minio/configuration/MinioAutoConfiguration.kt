package com.labijie.appliction.minio.configuration

import com.labijie.application.component.IObjectStorage
import com.labijie.application.configuration.DefaultsAutoConfiguration
import com.labijie.appliction.minio.MinioInitializer
import com.labijie.appliction.minio.MinioObjectStorage
import com.labijie.appliction.minio.MinioUtils
import com.labijie.appliction.minio.web.MinioStsController
import com.labijie.infra.getApplicationName
import io.minio.MinioClient
import okhttp3.OkHttpClient
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.AutoConfigureBefore
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(MinioProperties::class)
@ConditionalOnMissingBean(IObjectStorage::class)
@AutoConfigureBefore(DefaultsAutoConfiguration::class)
class MinioAutoConfiguration {

    @Bean
    fun minioClient(
        properties: MinioProperties,
        @Autowired(required = false)
        httpClient: OkHttpClient?
    ): MinioClient {
        return MinioClient.builder()
            .endpoint(properties.endpoint)
            .endpoint(properties.endpoint)
            .region(properties.region)
            .apply {
                if (httpClient != null) {
                    this.httpClient(httpClient)
                }
            }
            .credentials(properties.accessKey, properties.secretKey).build()
    }

    @Bean
    fun minioUtils(environment: Environment, properties: MinioProperties, okHttpClient: OkHttpClient): MinioUtils {
        return MinioUtils(environment.getApplicationName(), properties, okHttpClient)
    }

    @Bean
    fun minioObjectStorage(
        environment: Environment,
        minioClient: MinioClient,
        minioProperties: MinioProperties
    ): MinioObjectStorage {
        return MinioObjectStorage(environment.getApplicationName(), minioProperties, minioClient)
    }

    @Bean
    fun minioInitializer(environment: Environment): MinioInitializer {
        return MinioInitializer()
    }

    @ConditionalOnMissingBean(MinioStsController::class)
    @Configuration(proxyBeanMethods = false)
    @ConditionalOnProperty(prefix = "application.minio", name = ["controller-enabled"], havingValue = "true", matchIfMissing = true)
    @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
    protected class MinioWebAutoConfiguration {

        @Bean
        fun minioStsController(): MinioStsController {
            return MinioStsController()
        }
    }
}