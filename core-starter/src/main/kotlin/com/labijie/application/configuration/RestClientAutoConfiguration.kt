/**
 * @author Anders Xiao
 * @date 2024-02-02
 */
package com.labijie.application.configuration

import com.labijie.application.SpringContext
import com.labijie.application.httpclient.HttpClientLoggingInterceptor
import org.springframework.boot.autoconfigure.AutoConfigureAfter
import org.springframework.boot.autoconfigure.web.client.RestClientAutoConfiguration
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.web.client.RestClientCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.Ordered
import org.springframework.http.client.ReactorNettyClientRequestFactory
import org.springframework.web.client.RestClient
import reactor.netty.http.client.HttpClient


@Configuration(proxyBeanMethods = false)
@AutoConfigureAfter(RestClientAutoConfiguration::class)
class RestClientAutoConfiguration : HttpClientAutoConfigurationBase() {

    @Bean
    fun applicationRestClientCustomizer(httpClientProperties: HttpClientProperties) : RestClientCustomizer {
        return object: RestClientCustomizer, Ordered {
            override fun customize(builder: RestClient.Builder) {
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

            override fun getOrder(): Int {
                return Int.MAX_VALUE
            }
        }
    }
}