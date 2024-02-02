package com.labijie.application.configuration

import com.labijie.application.SpringContext
import com.labijie.application.httpclient.HttpClientLoggingInterceptor
import com.labijie.infra.json.JacksonHelper
import org.springframework.boot.autoconfigure.AutoConfigureAfter
import org.springframework.boot.autoconfigure.AutoConfigureBefore
import org.springframework.boot.autoconfigure.web.client.RestClientAutoConfiguration
import org.springframework.boot.autoconfigure.web.client.RestTemplateAutoConfiguration
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.web.client.RestClientCustomizer
import org.springframework.boot.web.client.RestTemplateCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.Ordered
import org.springframework.http.client.ReactorNettyClientRequestFactory
import org.springframework.http.converter.AbstractHttpMessageConverter
import org.springframework.http.converter.ByteArrayHttpMessageConverter
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.http.converter.StringHttpMessageConverter
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.web.client.RestClient
import reactor.netty.http.client.HttpClient

/**
 * @author Anders Xiao
 * @date 2023-12-02
 */
@Configuration(proxyBeanMethods = false)
@AutoConfigureBefore(RestTemplateAutoConfiguration::class)
class RestTemplateAutoConfiguration : HttpClientAutoConfigurationBase() {

    @Bean
    fun applicationRestTemplateCustomizer(httpClientProperties: HttpClientProperties): RestTemplateCustomizer {
        return RestTemplateCustomizer { rt ->
            rt.messageConverters.applyDefaultConverter()
            rt.interceptors.add(HttpClientLoggingInterceptor(httpClientProperties))
        }
    }
}