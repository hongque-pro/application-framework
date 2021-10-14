package com.labijie.application.configuration

import com.labijie.application.ApplicationInitializationRunner
import com.labijie.application.ErrorRegistry
import com.labijie.application.IErrorRegistry
import com.labijie.application.async.handler.MessageHandler
import com.labijie.application.component.IMessageSender
import com.labijie.application.model.SendSmsTemplateParam
import com.labijie.application.okhttp.IOkHttpClientCustomizer
import com.labijie.application.okhttp.OkHttpLoggingInterceptor
import com.labijie.application.web.client.MultiRestTemplates
import com.labijie.infra.json.JacksonHelper
import okhttp3.ConnectionPool
import okhttp3.OkHttpClient
import org.springframework.beans.factory.ObjectProvider
import org.springframework.boot.autoconfigure.AutoConfigureBefore
import org.springframework.boot.autoconfigure.condition.*
import org.springframework.boot.autoconfigure.web.client.RestTemplateAutoConfiguration
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.boot.web.client.RestTemplateCustomizer
import org.springframework.boot.web.reactive.context.AnnotationConfigReactiveWebServerApplicationContext
import org.springframework.boot.web.servlet.context.AnnotationConfigServletWebServerApplicationContext
import org.springframework.cloud.commons.httpclient.OkHttpClientConnectionPoolFactory
import org.springframework.cloud.commons.httpclient.OkHttpClientFactory
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Lazy
import org.springframework.core.annotation.Order
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory
import org.springframework.http.converter.AbstractHttpMessageConverter
import org.springframework.http.converter.ByteArrayHttpMessageConverter
import org.springframework.http.converter.StringHttpMessageConverter
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.messaging.converter.ByteArrayMessageConverter
import org.springframework.messaging.converter.StringMessageConverter
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.web.client.RestTemplate
import java.io.ByteArrayOutputStream
import java.util.concurrent.TimeUnit
import java.util.function.Consumer
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
@EnableAsync
@Order(-1)
class ApplicationCoreAutoConfiguration {

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
  @ConditionalOnBean(IMessageSender::class)
  @ConditionalOnProperty(value = ["application.sms.async.sink-enabled"], matchIfMissing = true)
  protected class MessageSinkConfiguration {
    @Bean
    fun handleSms(messageSender: IMessageSender): Consumer<SendSmsTemplateParam> {
      return Consumer {
        messageSender.sendSmsTemplate(it, async = false, checkTimeout = true)
      }
    }
  }

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

          //替换默认的 json mapper 转换
          rt.messageConverters.removeIf {
            it is MappingJackson2HttpMessageConverter
          }

          rt.messageConverters.add(0, MappingJackson2HttpMessageConverter(JacksonHelper.defaultObjectMapper))

          //修正 UTF-8 编码
          rt.messageConverters.filterIsInstance<AbstractHttpMessageConverter<*>>().forEach {
            when (it) {
                is StringHttpMessageConverter -> {
                  it.defaultCharset = Charsets.UTF_8
                  it.setWriteAcceptCharset(false)
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
      fun restTemplate(builder: RestTemplateBuilder): RestTemplate {
        return builder.build().apply {
          this.messageConverters.filterIsInstance<StringHttpMessageConverter>().forEach {
            it.setWriteAcceptCharset(false)
          }
        }
      }


      @Bean
      @ConditionalOnMissingBean(MultiRestTemplates::class)
      fun multiRestTemplates(
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