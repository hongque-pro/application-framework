package com.labijie.application.huawei.configuration

import com.labijie.application.configuration.SmsTemplates
import com.labijie.application.huawei.component.HuaweiMessageSender
import com.labijie.application.huawei.component.HuaweiObsStorage
import com.labijie.application.huawei.model.HuaweiMessageTemplates
import com.labijie.application.huawei.model.HuaweiProperties
import com.labijie.application.huawei.utils.HuaweiUtils
import com.labijie.caching.ICacheManager
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
@Configuration(proxyBeanMethods = false)
class HuaweiAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    @ConfigurationProperties("huawei")
    fun huaweiProperties(): HuaweiProperties = HuaweiProperties()

    @Bean
    @Primary
    fun huaweiUtils(restTemplate: RestTemplate, huaweiProperties: HuaweiProperties): HuaweiUtils = HuaweiUtils(huaweiProperties, restTemplate)

    @Bean
    @Primary
    @ConditionalOnProperty(name = ["huawei.obs.enabled"], havingValue = "true", matchIfMissing = true)
    fun huaweiObsStorage(restTemplate: RestTemplate, huaweiProperties: HuaweiProperties
    ): HuaweiObsStorage = HuaweiObsStorage(huaweiUtils(restTemplate, huaweiProperties))


    @Bean
    @ConditionalOnMissingBean
    @ConfigurationProperties("huawei.sms.tpconfigs")
    fun huaweiMessageTemplates(): HuaweiMessageTemplates {
        return HuaweiMessageTemplates()
    }

    @Bean
    @Primary
    @ConditionalOnProperty(name = ["huawei.sms.enabled"], havingValue = "true", matchIfMissing = true)
    fun huaweiMessageSender(environment: Environment,
                            huaweiUtils: HuaweiUtils,
                            cacheManager: ICacheManager,
                            rfc6238TokenService: Rfc6238TokenService,
                            restTemplate: RestTemplate,
                            huaweiMessageTemplates: HuaweiMessageTemplates
    ): HuaweiMessageSender = HuaweiMessageSender(environment, cacheManager, rfc6238TokenService, huaweiUtils, huaweiMessageTemplates)

    @Bean
    @ConditionalOnMissingBean
    @ConfigurationProperties("huawei.sms.templates")
    fun smsTemplates(): SmsTemplates {
        return SmsTemplates()
    }
}