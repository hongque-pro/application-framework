package com.labijie.application.configuration

import com.labijie.application.component.IHumanChecker
import com.labijie.application.component.IObjectStorage
import com.labijie.application.component.impl.NoneHumanChecker
import com.labijie.application.component.impl.NoneObjectStorage
import com.labijie.application.service.IFileIndexService
import com.labijie.application.service.IOneTimeCodeService
import com.labijie.application.service.impl.DefaultOnetimeCodeService
import com.labijie.application.service.impl.FileIndexService
import com.labijie.infra.IIdGenerator
import com.labijie.infra.security.IRfc6238TokenService
import org.springframework.boot.autoconfigure.AutoConfigureOrder
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.Ordered
import org.springframework.transaction.support.TransactionTemplate
import javax.sql.DataSource

@Configuration(proxyBeanMethods = false)
@AutoConfigureOrder(Ordered.LOWEST_PRECEDENCE)
class DefaultsAutoConfiguration {


    @Bean
    @ConditionalOnMissingBean(IHumanChecker::class)
    fun noneHumanChecker(): NoneHumanChecker {
        return NoneHumanChecker
    }

    @Bean
    @ConditionalOnMissingBean(IObjectStorage::class)
    fun noneObjectStorage(): IObjectStorage {
        return NoneObjectStorage()
    }

    @Bean
    @ConditionalOnBean(TransactionTemplate::class, DataSource::class)
    @ConditionalOnMissingBean(IFileIndexService::class)
    fun fileIndexService(
        applicationCoreProperties: ApplicationCoreProperties,
        objectStorage: IObjectStorage,
        idGenerator: IIdGenerator,
        transactionTemplate: TransactionTemplate,
    ) : FileIndexService {
        return FileIndexService(applicationCoreProperties, transactionTemplate, idGenerator, objectStorage)
    }


    @Bean
    @ConditionalOnMissingBean(IOneTimeCodeService::class)
    fun defaultOnetimeCodeService(
        coreProperties: ApplicationCoreProperties,
        rfc6238TokenService: IRfc6238TokenService,
        properties: OneTimeCodeProperties): DefaultOnetimeCodeService {
        return DefaultOnetimeCodeService(coreProperties, properties, rfc6238TokenService)
    }
}