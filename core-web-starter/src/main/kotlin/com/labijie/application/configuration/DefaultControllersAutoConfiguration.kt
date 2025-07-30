/**
 * @author Anders Xiao
 * @date 2024-01-23
 */
package com.labijie.application.configuration

import com.labijie.application.service.IFileIndexService
import com.labijie.application.web.controller.ApplicationController
import com.labijie.application.web.controller.FileController
import org.springframework.boot.autoconfigure.AutoConfigureAfter
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@AutoConfigureAfter(ApplicationWebAutoConfiguration::class, DefaultsAutoConfiguration::class)
@Configuration(proxyBeanMethods = false)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ConditionalOnProperty(prefix = "application.web.service-controllers", name = ["enabled"], havingValue = "true", matchIfMissing = true)
class DefaultControllersAutoConfiguration {
    @Bean
    @ConditionalOnMissingBean(ApplicationController::class)
    fun applicationController(): ApplicationController {
        return ApplicationController()
    }

    @Bean
    @ConditionalOnMissingBean(FileController::class)
    fun fileController(fileIndexService: IFileIndexService): FileController {
        return FileController(fileIndexService)
    }


}