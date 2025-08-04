package com.labijie.application.configuration

import com.labijie.application.component.IObjectStorage
import com.labijie.application.component.storage.FileStorageController
import com.labijie.application.component.storage.FileSystemObjectStorage
import com.labijie.application.component.storage.FileSystemStorageProperties
import com.labijie.application.component.storage.IHostProvider
import com.labijie.application.web.WebServerUtils
import org.springframework.boot.autoconfigure.AutoConfigureBefore
import org.springframework.boot.autoconfigure.AutoConfigureOrder
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.Ordered
import org.springframework.core.env.Environment

/**
 * @author Anders Xiao
 * @date 2025-07-11
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@AutoConfigureBefore(DefaultsAutoConfiguration::class)
@EnableConfigurationProperties(FileSystemStorageProperties::class)
@ConditionalOnProperty(name = ["application.object-storage.file.enabled"], havingValue = "true", matchIfMissing = false)
@ConditionalOnMissingBean(IObjectStorage::class)
@AutoConfigureOrder(Ordered.LOWEST_PRECEDENCE - 1)
class FileSystemStorageAutoConfiguration(private val properties: FileSystemStorageProperties) {

    private class WebServerHostProvider: IHostProvider {
        override fun getHost(): String {
            return WebServerUtils.getServerBaseUrl()
        }

    }

    @Bean
    fun fileSystemObjectStorage(environment: Environment): FileSystemObjectStorage {
        return FileSystemObjectStorage(properties, environment, WebServerHostProvider())
    }

    @Bean
    fun fileStorageController(fileSystemObjectStorage: FileSystemObjectStorage): FileStorageController {
        return FileStorageController(fileSystemObjectStorage)
    }
}