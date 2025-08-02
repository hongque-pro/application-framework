package com.labijie.application.configuration

import com.labijie.application.ApplicationErrors
import com.labijie.application.ApplicationInitializationRunner
import com.labijie.application.ErrorRegistry
import com.labijie.application.IErrorRegistry
import com.labijie.application.annotation.ImportErrorDefinition
import com.labijie.application.service.IOneTimeCodeService
import com.labijie.application.service.impl.DefaultOnetimeCodeService
import com.labijie.application.data.LocalizationMessageTable
import com.labijie.application.okhttp.OkHttpClientRequestFactoryBuilder
import com.labijie.application.open.OpenApiErrors
import com.labijie.infra.orm.annotation.TableScan
import com.labijie.infra.security.IRfc6238TokenService
import org.springframework.boot.WebApplicationType
import org.springframework.boot.autoconfigure.AutoConfigureAfter
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnNotWebApplication
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication
import org.springframework.boot.autoconfigure.web.client.RestClientAutoConfiguration
import org.springframework.boot.autoconfigure.web.client.RestTemplateAutoConfiguration
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Lazy
import org.springframework.web.client.RestTemplate


/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-09-09
 */
@TableScan(basePackageClasses = [LocalizationMessageTable::class])
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(
    ApplicationCoreProperties::class,
    ValidationProperties::class,
    OpenApiClientProperties::class,
    OneTimeCodeProperties::class
)
@AutoConfigureAfter(RestTemplateAutoConfiguration::class, RestClientAutoConfiguration::class)
@ImportErrorDefinition([OpenApiErrors::class, ApplicationErrors::class])
open class ApplicationCoreAutoConfiguration {


    @Bean
    @ConditionalOnNotWebApplication
    fun applicationInitializationRunner(): ApplicationInitializationRunner {
        return ApplicationInitializationRunner(WebApplicationType.NONE)
    }

    @Bean
    @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
    fun webApplicationInitializationRunner(): ApplicationInitializationRunner {
        return ApplicationInitializationRunner(WebApplicationType.SERVLET)
    }

    @Bean
    @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
    fun reactWebApplicationInitializationRunner(): ApplicationInitializationRunner {
        return ApplicationInitializationRunner(WebApplicationType.REACTIVE)
    }

    @Bean
    @ConditionalOnMissingBean(IErrorRegistry::class)
    fun errorRegistry(): IErrorRegistry {
        return ErrorRegistry()
    }


//    @Bean
//    @ConditionalOnMissingBean(HttpClient::class)
//    fun nettyHttpClient(nettyHttpClientProperties: HttpClientProperties): HttpClient {
//        return NettyUtils.createHttpClient(
//            nettyHttpClientProperties.connectTimeout,
//            nettyHttpClientProperties.readTimeout,
//            nettyHttpClientProperties.writeTimeout,
//            loggerEnabled = nettyHttpClientProperties.loggerEnabled
//        )
//    }

    @Configuration(proxyBeanMethods = false)
    protected class ApplicationRestTemplateConfiguration {

        @ConditionalOnMissingBean(RestTemplate::class)
        @ConditionalOnBean(OkHttpClientRequestFactoryBuilder::class)
        @Bean
        @Lazy
        fun restTemplate(
            clientHttpRequestFactory: OkHttpClientRequestFactoryBuilder,
            builder: RestTemplateBuilder
        ): RestTemplate {
            return builder.build().apply {
                requestFactory = clientHttpRequestFactory.build()
            }
        }
    }

    @Bean
    @ConditionalOnMissingBean(IOneTimeCodeService::class)
    fun defaultOnetimeCodeService(
        coreProperties: ApplicationCoreProperties,
        rfc6238TokenService: IRfc6238TokenService,
        properties: OneTimeCodeProperties): DefaultOnetimeCodeService {
        return DefaultOnetimeCodeService(coreProperties, properties, rfc6238TokenService)
    }
}