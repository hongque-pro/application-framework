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
@Configuration
class HuaweiAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    @ConfigurationProperties("huawei")
    fun huaweiProperties(): HuaweiProperties = HuaweiProperties()

    @Bean
    @Primary
    fun huaweiUtils(restTemplate: RestTemplate): HuaweiUtils = HuaweiUtils(huaweiProperties(), restTemplate)

    @Bean
    @Primary
    @ConditionalOnProperty(name = ["huawei.sms.enabled"], havingValue = "true", matchIfMissing = true)
    fun huaweiMessageSender(environment: Environment,
                            cacheManager: ICacheManager,
                            rfc6238TokenService: Rfc6238TokenService,
                            restTemplate: RestTemplate
    ): HuaweiMessageSender = HuaweiMessageSender(environment, cacheManager, rfc6238TokenService, huaweiUtils(restTemplate), huaweiMessageTemplates())


    @Bean
    @Primary
    @ConditionalOnProperty(name = ["huawei.obs.enabled"], havingValue = "true", matchIfMissing = true)
    fun huaweiObsStorage(restTemplate: RestTemplate
    ): HuaweiObsStorage = HuaweiObsStorage(huaweiUtils(restTemplate))


    @Bean
    @ConditionalOnMissingBean
    @ConfigurationProperties("huawei.sms.tpconfigs")
    fun huaweiMessageTemplates(): HuaweiMessageTemplates {
        return HuaweiMessageTemplates()
    }

    @Bean
    @ConditionalOnMissingBean
    @ConfigurationProperties("huawei.sms.templates")
    fun smsTemplates(): SmsTemplates {
        return SmsTemplates()
    }
}