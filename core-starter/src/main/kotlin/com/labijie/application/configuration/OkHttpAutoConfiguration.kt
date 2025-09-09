package com.labijie.application.configuration

import com.labijie.application.okhttp.IOkHttpClientBuildCustomizer
import com.labijie.application.okhttp.OkHttpClientRequestFactoryBuilder
import com.labijie.application.okhttp.OkHttpProperties
import com.labijie.infra.utils.ifNullOrBlank
import jakarta.annotation.PreDestroy
import okhttp3.Cache
import okhttp3.ConnectionPool
import okhttp3.OkHttpClient
import okhttp3.Protocol
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.ObjectProvider
import org.springframework.boot.autoconfigure.AutoConfigureBefore
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.autoconfigure.web.client.RestClientAutoConfiguration
import org.springframework.boot.autoconfigure.web.client.RestTemplateAutoConfiguration
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.http.client.ClientHttpRequestFactoryBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.util.FileSystemUtils
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.util.concurrent.TimeUnit
import kotlin.io.path.Path
import kotlin.io.path.exists
import kotlin.io.path.isDirectory

/**
 * @author Anders Xiao
 * @date 2025/6/21
 */
@EnableConfigurationProperties(OkHttpProperties::class)
@Configuration(proxyBeanMethods = false)
@AutoConfigureBefore(
    RestClientAutoConfiguration::class,
    RestTemplateAutoConfiguration::class,
    ApplicationCoreAutoConfiguration::class,
    RestTemplateCustomizationAutoConfiguration::class,
    RestClientCustomizationAutoConfiguration::class
)
class OkHttpAutoConfiguration : HttpClientAutoConfigurationBase() {


    private var tempDirCache: File? = null

    companion object {
        private val logger by lazy {
            LoggerFactory.getLogger(OkHttpAutoConfiguration::class.java)
        }
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(value = ["application.okhttp.cache.enabled"], havingValue = "true", matchIfMissing = false)
    @Throws(IOException::class)
    fun okHttp3Cache(okHttpProperties: OkHttpProperties): Cache {
        val directory = okHttpProperties.cache.directory.ifNullOrBlank { "okhttp-cache" }
        val fullDir = Path(System.getProperty("java.io.tmpdir"), directory)
        val file = if (!fullDir.exists() || !fullDir.isDirectory()) {
            Files.createTempDirectory(directory).toFile()
        } else {
            fullDir.toFile()
        }
        tempDirCache = file
        return Cache(file, okHttpProperties.cache.maxSize.toBytes())
    }


    @Bean
    @ConditionalOnMissingBean
    fun okHttp3ConnectionPool(okHttpProperties: OkHttpProperties): ConnectionPool {
        val maxIdleConnections: Int = okHttpProperties.connectionPool.maxIdleConnections
        val keepAliveDuration = okHttpProperties.connectionPool.keepAliveDuration
        return ConnectionPool(maxIdleConnections, keepAliveDuration.toMillis(), TimeUnit.MILLISECONDS)
    }

    @Bean
    @ConditionalOnMissingBean
    fun okHttp3Client(
        customizers: ObjectProvider<IOkHttpClientBuildCustomizer>,
        connectionPool: ConnectionPool,
        httpClientProperties: HttpClientProperties,
        okHttpProperties: OkHttpProperties,
        cache: ObjectProvider<Cache>,
    ): OkHttpClient {
        val builder = OkHttpClient.Builder()


        builder.connectTimeout(httpClientProperties.connectTimeout)
        builder.readTimeout(httpClientProperties.readTimeout)
        builder.writeTimeout(httpClientProperties.writeTimeout)

        builder.connectionPool(connectionPool)

        builder.followRedirects(true)
        builder.followSslRedirects(true)

        builder.protocols(listOf(Protocol.HTTP_1_1, Protocol.HTTP_2))

        builder.pingInterval(okHttpProperties.pingInterval)
        builder.retryOnConnectionFailure(okHttpProperties.retryOnConnectionFailure)
        cache.ifUnique?.let { builder.cache(it) }

        customizers.orderedStream().forEach {
            it.customize(builder)
        }


        return builder.build()
    }

    @Bean
    @ConditionalOnMissingBean(ClientHttpRequestFactoryBuilder::class)
    fun okHttpClientRequestFactoryBuilder(okHttpClient: OkHttpClient): OkHttpClientRequestFactoryBuilder {
        return OkHttpClientRequestFactoryBuilder(okHttpClient)
    }


    @PreDestroy
    fun deleteOkHttpCache() {
        tempDirCache?.let {
            logger.info("Deleting the temporary OkHttp Cache at ${it.absolutePath}")
            try {
                FileSystemUtils.deleteRecursively(it)
            } catch (e: Exception) {
                logger.warn("Failed to delete the temporary OkHttp Cache at ${it.absolutePath}")
            }
        }
    }
}