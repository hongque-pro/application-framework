package com.labijie.application.configuration

import com.labijie.application.ApplicationInitializationRunner
import com.labijie.application.CoreErrorRegistration
import com.labijie.application.ErrorRegistry
import com.labijie.application.IErrorRegistry
import com.labijie.application.netty.NettyUtils
import com.labijie.application.netty.NettyUtils.configure
import com.labijie.application.web.client.MultiRestTemplates
import com.labijie.infra.json.JacksonHelper
import org.springframework.boot.autoconfigure.AutoConfigureBefore
import org.springframework.boot.autoconfigure.condition.*
import org.springframework.boot.autoconfigure.web.client.RestTemplateAutoConfiguration
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.boot.web.client.RestTemplateCustomizer
import org.springframework.boot.web.reactive.context.AnnotationConfigReactiveWebServerApplicationContext
import org.springframework.boot.web.servlet.context.AnnotationConfigServletWebServerApplicationContext
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Lazy
import org.springframework.core.annotation.Order
import org.springframework.http.client.ReactorNettyClientRequestFactory
import org.springframework.http.converter.AbstractHttpMessageConverter
import org.springframework.http.converter.ByteArrayHttpMessageConverter
import org.springframework.http.converter.StringHttpMessageConverter
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.web.client.RestTemplate
import reactor.netty.http.client.HttpClient


/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-09-09
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(ValidationConfiguration::class, SmsBaseSettings::class, OpenApiClientProperties::class, HttpClientProperties::class)
@AutoConfigureBefore(RestTemplateAutoConfiguration::class)
@Order(-1)
class ApplicationCoreAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(CoreErrorRegistration::class)
    fun coreErrorRegistration(): CoreErrorRegistration {
        return CoreErrorRegistration()
    }

    @Bean
    @ConditionalOnNotWebApplication
    fun applicationInitializationRunner(): ApplicationInitializationRunner<ConfigurableApplicationContext> {
        return ApplicationInitializationRunner(ConfigurableApplicationContext::class)
    }

    @Bean
    @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
    fun webApplicationInitializationRunner(): ApplicationInitializationRunner<AnnotationConfigServletWebServerApplicationContext> {
        return ApplicationInitializationRunner(AnnotationConfigServletWebServerApplicationContext::class)
    }

    @Bean
    @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
    fun reactWebApplicationInitializationRunner(): ApplicationInitializationRunner<AnnotationConfigReactiveWebServerApplicationContext> {
        return ApplicationInitializationRunner(AnnotationConfigReactiveWebServerApplicationContext::class)
    }

    @Bean
    @ConditionalOnMissingBean(IErrorRegistry::class)
    fun errorRegistry(): IErrorRegistry {
        return ErrorRegistry()
    }


    @Configuration(proxyBeanMethods = false)
    @ConditionalOnProperty(value = ["application.okhttp.enabled"], matchIfMissing = true)
    @ConditionalOnMissingBean(HttpClient::class)
    protected class NettyHttpClientAutoConfiguration {


        @Bean
        fun nettyHttpClient(nettyHttpClientProperties: HttpClientProperties): HttpClient {
            return NettyUtils.createHttpClient(null, null, nettyHttpClientProperties.loggerEnabled)
        }

        @Configuration(proxyBeanMethods = false)
        protected class RestTemplateConfiguration {

            @Bean
            fun nettyHttpCustomizer(): RestTemplateCustomizer {
                return RestTemplateCustomizer { rt ->

                    rt.messageConverters.removeIf {
                        it is MappingJackson2HttpMessageConverter
                    }

                    rt.messageConverters.add(0, MappingJackson2HttpMessageConverter(JacksonHelper.defaultObjectMapper))

                    //修正 UTF-8 编码
                    rt.messageConverters.filterIsInstance<AbstractHttpMessageConverter<*>>().forEach {
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
            }

            @Lazy
            @ConditionalOnMissingBean(RestTemplate::class)
            @Bean
            fun restTemplate(
                nettyHttpClient: HttpClient,
                nettyHttpClientProperties: HttpClientProperties,
                builder: RestTemplateBuilder): RestTemplate {
                val factory = ReactorNettyClientRequestFactory(nettyHttpClient).apply {
                    this.configure(nettyHttpClientProperties)
                }
                return builder.build().apply {
                    requestFactory = factory
                }
            }


            @Bean
            @ConditionalOnMissingBean(MultiRestTemplates::class)
            fun multiRestTemplates(
                okHttpClientProperties: HttpClientProperties,
                restTemplateBuilder: RestTemplateBuilder,
                restTemplate: RestTemplate
            ): MultiRestTemplates {
                return MultiRestTemplates(
                    restTemplateBuilder,
                    okHttpClientProperties,
                    restTemplate
                )
            }
        }
    }


}