package com.labijie.application.sms.configuration

import com.labijie.application.service.IOneTimeCodeService
import com.labijie.application.sms.provider.ISmsServiceProvider
import com.labijie.application.sms.service.DefaultSmsService
import com.labijie.application.sms.service.ISmsService
import com.labijie.caching.ICacheManager
import org.springframework.beans.factory.ObjectProvider
import org.springframework.boot.autoconfigure.AutoConfigureOrder
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.Ordered

/**
 * @author Anders Xiao
 * @date 2025/7/30
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(SmsServiceProperties::class)
@AutoConfigureOrder(Ordered.LOWEST_PRECEDENCE - 100)
class ApplicationSmsAutoConfiguration {


    @Bean
    @ConditionalOnMissingBean(ISmsService::class)
    fun defaultSmsService(
        cacheManager: ICacheManager,
        oneTimeCodeService: IOneTimeCodeService,
        properties: SmsServiceProperties,
        providers: ObjectProvider<ISmsServiceProvider>
    ): DefaultSmsService {
        val providerList = providers.orderedStream().toList()
        return DefaultSmsService(properties, cacheManager, providerList, oneTimeCodeService)
    }
}