package com.labijie.application.auth.testing

import com.labijie.application.auth.component.MybatisClientDetailsService
import com.labijie.application.auth.configuration.AuthServerMybatisAutoConfiguration
import com.labijie.application.auth.configuration.AuthServerProperties
import com.labijie.application.auth.data.mapper.OAuth2ClientDetailsMapper
import com.labijie.caching.memory.MemoryCacheManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import

@Configuration(proxyBeanMethods = false)
@Import(AuthServerMybatisAutoConfiguration::class)
class UnitTestConfiguration{
    @Bean
    fun mybatisClientDetailsService(
        clientDetailsMapper: OAuth2ClientDetailsMapper
    ): MybatisClientDetailsService {
        val authServerProperties = AuthServerProperties()
        val cacheManager = MemoryCacheManager()
        return MybatisClientDetailsService(authServerProperties,
            cacheManager,
            clientDetailsMapper)
    }


}
