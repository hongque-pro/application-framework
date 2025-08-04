package com.labijie.application.web.controller

import com.labijie.application.component.IHumanChecker
import com.labijie.application.component.impl.NoneHumanChecker
import com.labijie.application.configuration.ApplicationWebDefaultsAutoConfiguration
import com.labijie.application.configuration.DefaultsAutoConfiguration
import com.labijie.application.service.IFileIndexService
import com.labijie.application.service.IOneTimeCodeService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.AutoConfigureAfter
import org.springframework.boot.autoconfigure.AutoConfigureOrder
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.Ordered

/**
 * @author Anders Xiao
 * @date 2025/8/4
 */
@Configuration(proxyBeanMethods = false)
@AutoConfigureOrder(Ordered.LOWEST_PRECEDENCE)
@AutoConfigureAfter(DefaultsAutoConfiguration::class, ApplicationWebDefaultsAutoConfiguration::class)
@EnableConfigurationProperties(ApplicationControllerProperties::class)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
class ApplicationControllersAutoConfiguration {


    @Bean
    @ConditionalOnProperty(prefix = ApplicationControllerProperties.CONFIG_KEY, name = ["file"], havingValue = "true", matchIfMissing = true)
    fun fileController(fileIndexService: IFileIndexService): FileController {
        return FileController(fileIndexService)
    }

    @Bean
    @ConditionalOnProperty(prefix = ApplicationControllerProperties.CONFIG_KEY, name = ["application"], havingValue = "true", matchIfMissing = true)
    fun applicationController(): ApplicationController {
        return ApplicationController()
    }

    @Bean
    @ConditionalOnProperty(prefix = ApplicationControllerProperties.CONFIG_KEY, name = ["one-time-code-verify"], havingValue = "true", matchIfMissing = false)
    fun verificationCodeController(verificationCodeService: IOneTimeCodeService): OneTimeCodeController {
        return OneTimeCodeController(verificationCodeService)
    }

    @Bean
    @ConditionalOnProperty(prefix = ApplicationControllerProperties.CONFIG_KEY, name = ["captcha-verify"], havingValue = "true", matchIfMissing = false)
    fun captchaVerificationController(
        @Autowired(required = false)
        humanChecker: IHumanChecker? = null): CaptchaVerificationController {
        return CaptchaVerificationController(humanChecker ?: NoneHumanChecker)
    }
}