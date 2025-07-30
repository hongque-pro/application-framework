package com.labijie.application.email.configuration

import com.labijie.application.component.IVerificationCodeService
import com.labijie.application.email.model.EmailVerificationSendRequest
import com.labijie.application.email.provider.IEmailServiceProvider
import com.labijie.application.email.service.IEmailService
import com.labijie.application.email.service.impl.DefaultEmailService
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
 * @date 2025/7/29
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(EmailServiceProperties::class)
@AutoConfigureOrder(Ordered.LOWEST_PRECEDENCE - 100)
class ApplicationEmailAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(IEmailService::class)
    fun defaultEmailService(
        properties: EmailServiceProperties,
        cacheManager: ICacheManager,
        verificationCodeService: IVerificationCodeService,
        emailServiceProviders: ObjectProvider<IEmailServiceProvider>
    ): DefaultEmailService {

        val providers = emailServiceProviders.orderedStream().toList()
        return DefaultEmailService(properties, cacheManager, verificationCodeService, providers)
    }
}