package com.labijie.application.configuration

import com.labijie.application.component.IHumanChecker
import com.labijie.application.component.IMessageSender
import com.labijie.application.component.IObjectStorage
import com.labijie.application.component.impl.NoneHumanChecker
import com.labijie.application.component.impl.NoneMessageSender
import com.labijie.application.component.impl.NoneObjectStorage
import com.labijie.caching.ICacheManager
import com.labijie.infra.security.Rfc6238TokenService
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.Ordered
import org.springframework.core.env.Environment

@Configuration(proxyBeanMethods = false)
class DefaultsAutoConfiguration: Ordered {

    override fun getOrder(): Int = Int.MAX_VALUE

    @Bean
    @ConditionalOnMissingBean(IMessageSender::class)
    fun noneMessageSender(
        environment: Environment,
        cacheManager: ICacheManager,
        rfc6238TokenService: Rfc6238TokenService
    ): IMessageSender {
        return NoneMessageSender(environment, cacheManager, rfc6238TokenService)
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
}