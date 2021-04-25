package com.labijie.application.configuration

import com.labijie.application.ApplicationInitializationRunner
import com.labijie.application.ErrorRegistry
import com.labijie.application.IErrorRegistry
import com.labijie.application.async.SmsSink
import com.labijie.application.async.SmsSource
import com.labijie.application.async.handler.MessageHandler
import com.labijie.application.component.IHumanChecker
import com.labijie.application.component.IMessageSender
import com.labijie.application.component.IObjectStorage
import com.labijie.application.component.impl.NoneHumanChecker
import com.labijie.application.component.impl.NoneMessageSender
import com.labijie.application.component.impl.NoneObjectStorage
import com.labijie.application.okhttp.IOkHttpClientCustomizer
import com.labijie.application.okhttp.OkHttpLoggingInterceptor
import com.labijie.application.web.client.MultiRestTemplates
import com.labijie.caching.ICacheManager
import com.labijie.infra.json.JacksonHelper
import com.labijie.infra.security.Rfc6238TokenService
import okhttp3.ConnectionPool
import okhttp3.OkHttpClient
import org.springframework.beans.factory.ObjectProvider
import org.springframework.boot.autoconfigure.AutoConfigureBefore
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnNotWebApplication
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication
import org.springframework.boot.autoconfigure.web.client.RestTemplateAutoConfiguration
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.boot.web.client.RestTemplateCustomizer
import org.springframework.boot.web.reactive.context.AnnotationConfigReactiveWebServerApplicationContext
import org.springframework.boot.web.servlet.context.AnnotationConfigServletWebServerApplicationContext
import org.springframework.cloud.commons.httpclient.OkHttpClientConnectionPoolFactory
import org.springframework.cloud.commons.httpclient.OkHttpClientFactory
import org.springframework.cloud.stream.annotation.EnableBinding
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Lazy
import org.springframework.core.annotation.Order
import org.springframework.core.env.Environment
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory
import org.springframework.http.converter.StringHttpMessageConverter
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.web.client.RestTemplate
import java.util.concurrent.TimeUnit
import java.util.stream.Collectors
import javax.annotation.PreDestroy

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-09-09
 */
@Suppress("SpringJavaInjectionPointsAutowiringInspection")
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(ValidationConfiguration::class)
@AutoConfigureBefore(RestTemplateAutoConfiguration::class)
@Order(Int.MAX_VALUE)
@EnableAsync
class ApplicationCoreAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(IMessageSender::class)
    fun noneMessageSender(
        environment: Environment,
        cacheManager: ICacheManager,
        rfc6238TokenService: Rfc6238TokenService
    ): IMessageSender {
        return NoneMessageSender(environment, cacheManager, rfc6238TokenService)
    }

    @Bean
    @ConditionalOnMissingBean(IHumanChecker::class)
    fun noneHumanChecker(): NoneHumanChecker {
        return NoneHumanChecker()
    }

    @Bean
    @ConditionalOnMissingBean(IObjectStorage::class)
    fun noneObjectStorage(): IObjectStorage {
        return NoneObjectStorage()
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

    @Bean
    @ConfigurationProperties("application.sms")
    fun smsBaseSettings(): SmsBaseSettings = SmsBaseSettings()

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnProperty(value = ["application.sms.async.enabled"], matchIfMissing = true)
    @EnableBinding(value = [SmsSource::class])
    protected class MessageSourceConfiguration

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnProperty(value = ["application.sms.async.enabled"], matchIfMissing = false)
    @ConditionalOnMissingBean(SmsSink::class)
    @ComponentScan(basePackageClasses = [MessageHandler::class])
    protected class MessageSinkConfiguration

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnProperty(value = ["application.okhttp.enabled"], matchIfMissing = true)
    @EnableConfigurationProperties(OkHttpClientProperties::class)
    @ConditionalOnMissingBean(OkHttpClient::class)
    protected class OkHttpClientAutoConfiguration {

        private var okHttpClient: OkHttpClient? = null

        @Bean
        @ConditionalOnMissingBean(ConnectionPool::class)
        fun connectionPool(
            okHttpClientProperties: OkHttpClientProperties,
            connectionPoolFactory: OkHttpClientConnectionPoolFactory
        ): ConnectionPool {
            val maxTotalConnections: Int = okHttpClientProperties.maxConnections
            val timeToLive: Long = okHttpClientProperties.timeToLive.toMillis()
            return connectionPoolFactory.create(maxTotalConnections, timeToLive, TimeUnit.MILLISECONDS)
        }

        @Bean
        fun okHttpClient(
            customizers: ObjectProvider<IOkHttpClientCustomizer>,
            httpClientFactory: OkHttpClientFactory,
            connectionPool: ConnectionPool,
            okHttpClientProperties: OkHttpClientProperties
        ): OkHttpClient {
            val disableSslValidation: Boolean = okHttpClientProperties.sslValidationDisabled
            this.okHttpClient = httpClientFactory.createBuilder(disableSslValidation)
                .connectTimeout(okHttpClientProperties.connectTimeout)
                .readTimeout(okHttpClientProperties.readTimeout)
                .writeTimeout(okHttpClientProperties.writeTimeout)
                .followRedirects(okHttpClientProperties.followRedirects)
                .connectionPool(connectionPool)
                .addInterceptor(OkHttpLoggingInterceptor())
                .apply {
                    customizers.orderedStream().forEach {
                        it.customize(this)
                    }
                }
                .build()

            return this.okHttpClient!!
        }

        @PreDestroy
        fun destroy() {
            okHttpClient?.dispatcher?.executorService?.shutdown()
            okHttpClient?.connectionPool?.evictAll()
        }

        @Configuration(proxyBeanMethods = false)
        protected class RestTemplateConfiguration {

            @Bean
            fun okhttpCustomizer(
                okHttpClient: OkHttpClient
            ): RestTemplateCustomizer {
                return RestTemplateCustomizer { rt ->
                    rt.requestFactory = OkHttp3ClientHttpRequestFactory(okHttpClient)
                    rt.messageConverters.add(0, MappingJackson2HttpMessageConverter(JacksonHelper.defaultObjectMapper))
                    rt.messageConverters.add(1, StringHttpMessageConverter(Charsets.UTF_8))

                    rt.messageConverters.filterIsInstance<StringHttpMessageConverter>().forEach {
                        it.setWriteAcceptCharset(false)
                    }
                }
            }

            @Lazy
            @ConditionalOnMissingBean(RestTemplate::class)
            @Bean
            fun restTemplate(builder: RestTemplateBuilder): RestTemplate {
                return builder.build().apply {
                    this.messageConverters.filterIsInstance<StringHttpMessageConverter>().forEach {
                        it.setWriteAcceptCharset(false)
                    }
                }
            }


            @Bean
            @ConditionalOnMissingBean(MultiRestTemplates::class)
            fun clientCertificateTemplates(
                customizers: ObjectProvider<IOkHttpClientCustomizer>,
                httpClientFactory: OkHttpClientFactory,
                okHttpClientProperties: OkHttpClientProperties,
                poolFactory: OkHttpClientConnectionPoolFactory,
                restTemplateBuilder: RestTemplateBuilder,
                restTemplate: RestTemplate
            ): MultiRestTemplates {
                return MultiRestTemplates(
                    httpClientFactory,
                    restTemplateBuilder,
                    okHttpClientProperties,
                    poolFactory,
                    customizers.orderedStream().collect(Collectors.toList()),
                    restTemplate
                )
            }
        }
    }
}