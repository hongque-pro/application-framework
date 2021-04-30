package com.labijie.application.auth.testing

import com.labijie.application.identity.configuration.IdentityAutoConfiguration
import com.labijie.application.identity.configuration.IdentityProperties
import com.labijie.application.identity.data.mapper.OAuth2ClientDetailsMapper
import com.labijie.application.identity.data.mapper.UserMapper
import com.labijie.application.identity.service.impl.DefaultOAuth2ClientService
import com.labijie.caching.ICacheManager
import com.labijie.caching.memory.MemoryCacheManager
import org.mybatis.spring.annotation.MapperScan
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.transaction.support.TransactionTemplate

@Configuration(proxyBeanMethods = false)
@MapperScan(basePackageClasses = [UserMapper::class])
class UnitTestConfiguration {

    @Bean
    fun memoryCache(): ICacheManager{
        return MemoryCacheManager()
    }

    @Bean
    fun defaultOAuth2ClientService(
            transactionTemplate: TransactionTemplate,
            clientDetailsMapper: OAuth2ClientDetailsMapper
    ): DefaultOAuth2ClientService {
        val identityProperties = IdentityProperties()
        val cacheManager = MemoryCacheManager()
        return DefaultOAuth2ClientService(
                transactionTemplate,
                identityProperties,
                cacheManager,
                clientDetailsMapper)
    }


}
