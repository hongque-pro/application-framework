package com.labijie.application.auth.testing

import com.labijie.application.identity.configuration.IdentityProperties
import com.labijie.application.identity.data.UserTable
import com.labijie.application.identity.service.impl.DefaultOAuth2ClientService
import com.labijie.caching.ICacheManager
import com.labijie.caching.memory.MemoryCacheManager
import com.labijie.infra.orm.annotation.TableScan
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.transaction.support.TransactionTemplate

@Configuration(proxyBeanMethods = false)
@TableScan(basePackageClasses = [UserTable::class])
class UnitTestConfiguration {

    @Bean
    fun memoryCache(): ICacheManager{
        return MemoryCacheManager()
    }

    @Bean
    fun defaultOAuth2ClientService(
            transactionTemplate: TransactionTemplate,
    ): DefaultOAuth2ClientService {
        val identityProperties = IdentityProperties()
        val cacheManager = MemoryCacheManager()
        return DefaultOAuth2ClientService(
                transactionTemplate,
                identityProperties,
                cacheManager)
    }


}
