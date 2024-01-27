/**
 * @author Anders Xiao
 * @date 2024-01-23
 */
package com.labijie.application.configuration

import com.labijie.application.component.IMessageService
import com.labijie.application.identity.service.IUserService
import com.labijie.application.service.IFileIndexService
import com.labijie.application.web.controller.ApplicationController
import com.labijie.application.web.controller.FileController
import com.labijie.application.web.controller.SmsController
import org.springframework.boot.autoconfigure.AutoConfigureAfter
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import


@AutoConfigureAfter(ApplicationWebAutoConfiguration::class, DefaultsAutoConfiguration::class)
@Configuration(proxyBeanMethods = false)
@ConditionalOnWebApplication
@ConditionalOnProperty(prefix = "application.web.service-controllers", name = ["enabled"], havingValue = "true", matchIfMissing = true)
class DefaultControllersAutoConfiguration {
    @Bean
    fun applicationController(): ApplicationController {
        return ApplicationController()
    }

    @Bean
    fun smsController(
        userService: IUserService,
        messageService: IMessageService
    ): SmsController {
        return SmsController(userService, messageService)
    }

    @Bean
    fun fileController(fileIndexService: IFileIndexService): FileController {
        return FileController(fileIndexService)
    }
}