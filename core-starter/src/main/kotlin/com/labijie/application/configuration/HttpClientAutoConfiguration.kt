package com.labijie.application.configuration

import com.labijie.application.SpringContext
import com.labijie.application.httpclient.HttpClientLoggingInterceptor
import com.labijie.infra.json.JacksonHelper
import org.springframework.boot.autoconfigure.AutoConfigureBefore
import org.springframework.boot.autoconfigure.web.client.RestClientAutoConfiguration
import org.springframework.boot.autoconfigure.web.client.RestTemplateAutoConfiguration
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.web.client.RestClientCustomizer
import org.springframework.boot.web.client.RestTemplateCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.client.ReactorNettyClientRequestFactory
import org.springframework.http.converter.AbstractHttpMessageConverter
import org.springframework.http.converter.ByteArrayHttpMessageConverter
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.http.converter.StringHttpMessageConverter
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import reactor.netty.http.client.HttpClient

/**
 * @author Anders Xiao
 * @date 2023-12-02
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(HttpClientProperties::class)
@AutoConfigureBefore(RestTemplateAutoConfiguration::class, RestClientAutoConfiguration::class)
class HttpClientAutoConfiguration {
    private fun MutableList<HttpMessageConverter<*>>.applyDefaultConverter(){
        this.removeIf {
            it is MappingJackson2HttpMessageConverter
        }

        this.add(0, MappingJackson2HttpMessageConverter(JacksonHelper.defaultObjectMapper))

        //修正 UTF-8 编码
        this.filterIsInstance<AbstractHttpMessageConverter<*>>().forEach {
            when (it) {
                is StringHttpMessageConverter -> {
                    it.defaultCharset = Charsets.UTF_8
                }
                !is ByteArrayHttpMessageConverter -> {
                    it.defaultCharset = Charsets.UTF_8
                }
            }
        }
    }

    @Bean
    fun applicationRestTemplateCustomizer(httpClientProperties: HttpClientProperties): RestTemplateCustomizer {
        return RestTemplateCustomizer { rt ->
            rt.messageConverters.applyDefaultConverter()
            rt.interceptors.add(HttpClientLoggingInterceptor(httpClientProperties))
        }
    }

    @Bean
    fun applicationRestClientCustomizer(httpClientProperties: HttpClientProperties) : RestClientCustomizer {
        return RestClientCustomizer {
            builder->
            if(SpringContext.isInitialized) {
                val client = SpringContext.current.getBeanProvider(HttpClient::class.java).ifAvailable
                client?.let {
                    builder.requestFactory(ReactorNettyClientRequestFactory(it))
                }
            }
            builder.requestInterceptors {
                it.add(HttpClientLoggingInterceptor(httpClientProperties))
            }
            builder.messageConverters {
                it.applyDefaultConverter()
            }
        }
    }
}