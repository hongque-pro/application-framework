/**
 * @author Anders Xiao
 * @date 2024-07-24
 */
package com.labijie.application.configuration

import com.labijie.application.component.ApplicationLocaleResolver
import com.labijie.application.service.ILocalizationService
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary


@ConditionalOnProperty(name = ["application.web.application-locale-resolver-enabled"], havingValue = "true", matchIfMissing = true)
@Configuration(proxyBeanMethods = false)
class ApplicationLocaleAutoConfiguration {

    /**
     *
     * TODO: Spring unable to override default AcceptHeaderLocaleResolver
     */
    @Bean
    @Primary
    fun applicationLocaleResolver(localizationService: ILocalizationService): ApplicationLocaleResolver {
        return ApplicationLocaleResolver(localizationService)
    }
}