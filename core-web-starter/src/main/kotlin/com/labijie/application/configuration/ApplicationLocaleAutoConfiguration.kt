/**
 * @author Anders Xiao
 * @date 2024-07-24
 */
package com.labijie.application.configuration

import com.labijie.application.component.ApplicationLocaleResolver
import com.labijie.application.service.ILocalizationService
import org.springframework.boot.autoconfigure.AutoConfigureBefore
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.LocaleResolver
import org.springframework.web.servlet.config.annotation.DelegatingWebMvcConfiguration
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver


@Configuration(proxyBeanMethods = false)
@AutoConfigureBefore(WebMvcAutoConfiguration::class)
class ApplicationLocaleAutoConfiguration(
    private val localizationService: ILocalizationService,
    private val properties: ApplicationWebProperties) : DelegatingWebMvcConfiguration() {

    @Bean
    override fun localeResolver(): LocaleResolver {
        if(properties.localeResolver.enabled) {
            return ApplicationLocaleResolver(localizationService)
        }
        return AcceptHeaderLocaleResolver()
    }
}