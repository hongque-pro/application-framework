/**
 * @author Anders Xiao
 * @date 2024-01-23
 */
package com.labijie.application.configuration

import com.labijie.application.web.controller.ApplicationController
import com.labijie.application.web.controller.FileController
import com.labijie.application.web.controller.SmsController
import org.springframework.boot.autoconfigure.AutoConfigureAfter
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import


@AutoConfigureAfter(ApplicationWebAutoConfiguration::class)
@Configuration(proxyBeanMethods = false)
@ConditionalOnWebApplication
class DefaultControllersAutoConfiguration {


    @ConditionalOnProperty(prefix = "application.web.service-controllers", name = ["enabled"], havingValue = "true", matchIfMissing = true)
    @Configuration(proxyBeanMethods = false)
    @Import(
        SmsController::class,
        FileController::class
    )
    protected class ControllerAutoConfiguration


    @Bean
    fun applicationController(): ApplicationController {
        return ApplicationController()
    }
}