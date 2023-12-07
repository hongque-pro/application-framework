package com.labijie.application.configuration

import com.labijie.application.component.IHumanChecker
import com.labijie.application.component.IMessageService
import com.labijie.application.component.IObjectStorage
import com.labijie.application.component.impl.NoneHumanChecker
import com.labijie.application.component.impl.NoneMessageService
import com.labijie.application.component.impl.NoneObjectStorage
import com.labijie.application.service.IFileIndexService
import com.labijie.application.service.impl.FileIndexService
import com.labijie.caching.ICacheManager
import com.labijie.infra.IIdGenerator
import com.labijie.infra.security.Rfc6238TokenService
import org.springframework.boot.autoconfigure.AutoConfigureOrder
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.Ordered
import org.springframework.transaction.support.TransactionTemplate

@Configuration(proxyBeanMethods = false)
@AutoConfigureOrder(Ordered.LOWEST_PRECEDENCE)
class DefaultsAutoConfiguration {
    @Bean
    @ConditionalOnMissingBean(IMessageService::class)
    fun noneMessageService(
        cacheManager: ICacheManager,
        rfc6238TokenService: Rfc6238TokenService
    ): IMessageService {
        return NoneMessageService(cacheManager, rfc6238TokenService)
    }

    @Bean
    @ConditionalOnMissingBean(IHumanChecker::class)
    fun noneHumanChecker(): NoneHumanChecker {
        return NoneHumanChecker()
    }

    @Bean
    @ConditionalOnMissingBean(IObjectStorage::class)
    fun noneObjectStorage(): IObjectStorage {
        return NoneObjectStorage()
    }

    @Bean
    @ConditionalOnMissingBean(IFileIndexService::class)
    fun fileIndexService(
        objectStorage: IObjectStorage,
        idGenerator: IIdGenerator,
        transactionTemplate: TransactionTemplate,
    ) : FileIndexService {
        return FileIndexService(transactionTemplate, idGenerator, objectStorage)
    }
}