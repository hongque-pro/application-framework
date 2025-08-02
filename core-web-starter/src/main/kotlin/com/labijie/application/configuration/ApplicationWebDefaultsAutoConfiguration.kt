package com.labijie.application.configuration

import com.labijie.application.component.IHumanChecker
import com.labijie.application.service.IOneTimeCodeService
import com.labijie.application.service.CaptchaHumanChecker
import com.labijie.application.web.controller.CaptchaVerificationController
import com.labijie.application.web.controller.ImageCaptchaGenerationController
import com.labijie.application.web.controller.OneTimeCodeController
import org.springframework.beans.factory.config.BeanDefinition
import org.springframework.boot.autoconfigure.AutoConfigureAfter
import org.springframework.boot.autoconfigure.AutoConfigureBefore
import org.springframework.boot.autoconfigure.AutoConfigureOrder
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Role
import org.springframework.core.Ordered

/**
 * @author Anders Xiao
 * @date 2025/7/29
 */
@Configuration(proxyBeanMethods = false)
@AutoConfigureBefore(DefaultsAutoConfiguration::class)
@AutoConfigureAfter(ApplicationWebAutoConfiguration::class, ApplicationCoreAutoConfiguration::class)
@AutoConfigureOrder(Ordered.LOWEST_PRECEDENCE - 1)
@EnableConfigurationProperties(ImageCaptchaProperties::class)
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
class ApplicationWebDefaultsAutoConfiguration {

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnMissingBean(IHumanChecker::class)
    @ConditionalOnProperty(prefix = "application.captcha.image", name = ["enabled"], havingValue = "true", matchIfMissing = false)
    class ImageCaptchaAutoConfiguration {

        @Bean
        fun captchaHumanChecker(applicationProperties: ApplicationCoreProperties): CaptchaHumanChecker {
            return CaptchaHumanChecker(applicationProperties)
        }

        @Bean
        fun imageCaptchaGenerationController(
            applicationProperties: ApplicationCoreProperties,
        ): ImageCaptchaGenerationController {
            return ImageCaptchaGenerationController(applicationProperties)
        }

    }

    @Configuration(proxyBeanMethods = false)
    @AutoConfigureAfter(ImageCaptchaAutoConfiguration::class)
    @ConditionalOnBean(IHumanChecker::class)
    class CaptchaVerificationAutoConfiguration {

        @Bean
        fun captchaVerificationController(humanChecker: IHumanChecker): CaptchaVerificationController {
            return CaptchaVerificationController(humanChecker)
        }
    }

    @Bean
    fun verificationCodeController(verificationCodeService: IOneTimeCodeService): OneTimeCodeController {
        return OneTimeCodeController(verificationCodeService)
    }
}