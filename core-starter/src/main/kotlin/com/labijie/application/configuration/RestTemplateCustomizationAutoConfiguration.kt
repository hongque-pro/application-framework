package com.labijie.application.configuration

import com.labijie.application.httpclient.HttpClientLoggingInterceptor
import org.springframework.boot.autoconfigure.AutoConfigureBefore
import org.springframework.boot.autoconfigure.web.client.RestTemplateAutoConfiguration
import org.springframework.boot.web.client.RestTemplateCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * @author Anders Xiao
 * @date 2023-12-02
 */
@Configuration(proxyBeanMethods = false)
@AutoConfigureBefore(RestTemplateAutoConfiguration::class)
class RestTemplateCustomizationAutoConfiguration : HttpClientAutoConfigurationBase() {

    @Bean
    fun applicationRestTemplateCustomizer(httpClientProperties: HttpClientProperties): RestTemplateCustomizer {
        return RestTemplateCustomizer { rt ->
            rt.messageConverters.applyDefaultConverter()
            rt.interceptors.add(HttpClientLoggingInterceptor(httpClientProperties))
        }
    }
}