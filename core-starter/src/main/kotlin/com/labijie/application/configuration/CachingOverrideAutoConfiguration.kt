package com.labijie.application.configuration

import com.labijie.caching.component.IDelayTimer
import com.labijie.caching.configuration.CachingAutoConfiguration
import com.labijie.infra.SecondIntervalTimeoutTimer
import org.springframework.boot.autoconfigure.AutoConfigureBefore
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.concurrent.TimeUnit

/**
 * @author Anders Xiao
 * @date 2023-12-06
 */
@AutoConfigureBefore(CachingAutoConfiguration::class)
@Configuration(proxyBeanMethods = false)
class CachingOverrideAutoConfiguration {

    class CachingDelayTimer : IDelayTimer {
        override fun delay(mills: Long, action: () -> Unit) {
            val time = mills.coerceAtLeast(1000)
            SecondIntervalTimeoutTimer.newTimeout(action, time, TimeUnit.MILLISECONDS)
        }

    }

    @Bean
    @ConditionalOnMissingBean(IDelayTimer::class)
    @ConditionalOnProperty(name = ["infra.caching.disabled"], havingValue = "false", matchIfMissing = true)
    fun cachingDelayTimer() : IDelayTimer {
        return CachingDelayTimer()
    }
}