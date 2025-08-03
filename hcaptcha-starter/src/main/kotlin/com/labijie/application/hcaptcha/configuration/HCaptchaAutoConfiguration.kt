package com.labijie.application.hcaptcha.configuration

import com.labijie.application.component.IHumanChecker
import com.labijie.application.configuration.ApplicationWebDefaultsAutoConfiguration
import com.labijie.application.hcaptcha.HCaptchaService
import org.springframework.boot.autoconfigure.AutoConfigureBefore
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestClient

/**
 * @author Anders Xiao
 * @date 2025/7/29
 */
@Configuration(proxyBeanMethods = false)
@AutoConfigureBefore(ApplicationWebDefaultsAutoConfiguration::class)
@EnableConfigurationProperties(HCaptchaProperties::class)
class HCaptchaAutoConfiguration {

    @ConditionalOnProperty(
        prefix = "application.captcha.hcaptcha",
        name = ["enabled"],
        havingValue = "true",
        matchIfMissing = true
    )
    @ConditionalOnMissingBean(IHumanChecker::class)
    fun hcaptchaService(properties: HCaptchaProperties, restClient: RestClient.Builder): HCaptchaService {
        return HCaptchaService(properties, restClient)
    }

}