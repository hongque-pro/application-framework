/**
 * @author Anders Xiao
 * @date 2024-02-02
 */
package com.labijie.application.configuration

import com.labijie.application.SpringContext
import com.labijie.application.httpclient.HttpClientLoggingInterceptor
import com.labijie.application.okhttp.OkHttpClientRequestFactoryBuilder
import org.springframework.boot.autoconfigure.AutoConfigureAfter
import org.springframework.boot.autoconfigure.web.client.RestClientAutoConfiguration
import org.springframework.boot.web.client.RestClientCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.Ordered
import org.springframework.web.client.RestClient


@Configuration(proxyBeanMethods = false)
@AutoConfigureAfter(RestClientAutoConfiguration::class)
class RestClientAutoConfiguration : HttpClientAutoConfigurationBase() {

    @Bean
    fun applicationRestClientCustomizer(
        okHttpClientRequestFactoryBuilder: OkHttpClientRequestFactoryBuilder,
        httpClientProperties: HttpClientProperties) : RestClientCustomizer {
        return object: RestClientCustomizer, Ordered {
            override fun customize(builder: RestClient.Builder) {
                if(SpringContext.isInitialized) {
                    builder.requestFactory(okHttpClientRequestFactoryBuilder.build())
                }
                builder.requestInterceptors {
                    it.add(HttpClientLoggingInterceptor(httpClientProperties))
                }
                builder.messageConverters {
                    it.applyDefaultConverter()
                }
            }

            override fun getOrder(): Int {
                return Int.MAX_VALUE
            }
        }
    }
}