package com.labijie.application.configuration

import com.labijie.application.ApplicationInitializationRunner
import com.labijie.application.CoreErrorRegistration
import com.labijie.application.ErrorRegistry
import com.labijie.application.IErrorRegistry
import com.labijie.application.httpclient.NettyUtils
import com.labijie.application.web.client.MultiRestTemplates
import com.labijie.infra.utils.logger
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.AutoConfigureAfter
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnNotWebApplication
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication
import org.springframework.boot.autoconfigure.web.client.RestClientAutoConfiguration
import org.springframework.boot.autoconfigure.web.client.RestTemplateAutoConfiguration
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.boot.web.reactive.context.AnnotationConfigReactiveWebServerApplicationContext
import org.springframework.boot.web.servlet.context.AnnotationConfigServletWebServerApplicationContext
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.client.ReactorNettyClientRequestFactory
import org.springframework.web.client.RestTemplate
import reactor.netty.http.client.HttpClient


/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-09-09
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(
    ApplicationCoreProperties::class,
    ValidationProperties::class,
    SmsBaseProperties::class,
    OpenApiClientProperties::class,
)
@AutoConfigureAfter(RestTemplateAutoConfiguration::class, RestClientAutoConfiguration::class)
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

    @Bean
    fun smsSettingsCheckRunner(settings: ApplicationCoreProperties): CommandLineRunner {
        return CommandLineRunner {
            if (settings.isDefaultDesSecret) {
                logger.warn("Sms use default security key, add bellow line to application.yml to fix this:\napplication.des-secret: <your key>")
            }

        }
    }


    @Bean
    @ConditionalOnMissingBean(HttpClient::class)
    fun nettyHttpClient(nettyHttpClientProperties: HttpClientProperties): HttpClient {
        return NettyUtils.createHttpClient(
            nettyHttpClientProperties.connectTimeout,
            nettyHttpClientProperties.readTimeout,
            nettyHttpClientProperties.writeTimeout,
            loggerEnabled = nettyHttpClientProperties.loggerEnabled
        )
    }

    @Configuration(proxyBeanMethods = false)
    protected class ApplicationRestTemplateConfiguration {

        @ConditionalOnMissingBean(RestTemplate::class)
        @Bean
        fun restTemplate(
            nettyHttpClient: HttpClient,
            nettyHttpClientProperties: HttpClientProperties,
            builder: RestTemplateBuilder
        ): RestTemplate {
            val factory = ReactorNettyClientRequestFactory(nettyHttpClient)
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