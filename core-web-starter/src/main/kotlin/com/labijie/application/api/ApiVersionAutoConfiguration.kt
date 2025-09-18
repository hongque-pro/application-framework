/**
 * @author Anders Xiao
 * @date 2025/9/18
 */
package com.labijie.application.api

import org.springframework.boot.autoconfigure.AutoConfigureBefore
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration(proxyBeanMethods = false)
@AutoConfigureBefore(WebMvcAutoConfiguration::class)
class ApiVersionAutoConfiguration {
    @Bean
    @ConditionalOnMissingBean
    fun apiVersionWebMvcRegistrations(): ApiVersionWebMvcRegistrations {
        return ApiVersionWebMvcRegistrations()
    }
}