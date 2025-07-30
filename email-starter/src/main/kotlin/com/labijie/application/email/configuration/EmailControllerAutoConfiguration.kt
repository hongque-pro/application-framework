package com.labijie.application.email.configuration

import com.labijie.application.conditional.ConditionalOnResourceServer
import com.labijie.application.email.mvc.EmailController
import com.labijie.application.email.service.IEmailService
import com.labijie.application.identity.service.IUserService
import org.springframework.boot.autoconfigure.AutoConfigureAfter
import org.springframework.boot.autoconfigure.AutoConfigureOrder
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
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
@AutoConfigureAfter(ApplicationEmailAutoConfiguration::class)
@AutoConfigureOrder(Ordered.LOWEST_PRECEDENCE)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ConditionalOnResourceServer
class EmailControllerAutoConfiguration {


    @Bean
    @ConditionalOnMissingBean(EmailController::class)
    @ConditionalOnProperty(prefix = "application.email", name = ["endpoint-enabled"], havingValue = "true", matchIfMissing = true)
    fun emailController(
        emailService: IEmailService,
        userService: IUserService,
    ): EmailController {
        return EmailController(emailService, userService)
    }
}