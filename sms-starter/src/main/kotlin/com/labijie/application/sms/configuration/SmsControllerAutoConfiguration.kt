package com.labijie.application.sms.configuration

import com.labijie.application.conditional.ConditionalOnResourceServer
import com.labijie.application.identity.service.IUserService
import com.labijie.application.sms.mvc.SmsController
import com.labijie.application.sms.service.ISmsService
import org.springframework.boot.autoconfigure.AutoConfigureAfter
import org.springframework.boot.autoconfigure.AutoConfigureOrder
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.Ordered

/**
 * @author Anders Xiao
 * @date 2025/7/30
 */
@Configuration(proxyBeanMethods = false)
@AutoConfigureAfter(ApplicationSmsAutoConfiguration::class)
@AutoConfigureOrder(Ordered.LOWEST_PRECEDENCE)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ConditionalOnResourceServer
class SmsControllerAutoConfiguration {

    @Bean
    @ConditionalOnProperty(prefix = "application.sms", name = ["endpoint-enabled"], havingValue = "true", matchIfMissing = true)
    fun smsController(
        smsService: ISmsService,
        userService: IUserService): SmsController {
        return SmsController(userService, smsService)
    }
}