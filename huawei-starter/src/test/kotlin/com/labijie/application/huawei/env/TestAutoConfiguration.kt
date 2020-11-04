package com.labijie.application.huawei.env

import com.labijie.application.huawei.component.HuaweiMessageSender
import com.labijie.application.huawei.component.HuaweiObsStorage
import com.labijie.application.huawei.model.HuaweiProperties
import com.labijie.application.huawei.utils.HuaweiUtils
import com.labijie.caching.ICacheManager
import com.labijie.caching.memory.MemoryCacheManager
import com.labijie.infra.security.Rfc6238TokenService
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.core.env.Environment
import org.springframework.web.client.RestTemplate

@Suppress("SpringJavaInjectionPointsAutowiringInspection")
@Configuration
class TestAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    fun restTemplate(): RestTemplate {
        return RestTemplate()
    }

    @Bean
    @ConditionalOnMissingBean
    fun cacheManager(): ICacheManager {
        return MemoryCacheManager()
    }

    @Bean
    @ConditionalOnMissingBean
    fun rfc6238TokenService(): Rfc6238TokenService {
        return Rfc6238TokenService()
    }
}