package com.labijie.application.web.client

import com.labijie.application.SSLUtilities
import com.labijie.application.configuration.OkHttpClientProperties
import com.labijie.application.okhttp.IOkHttpClientCustomizer
import com.labijie.application.okhttp.OkHttpLoggingInterceptor
import com.labijie.infra.json.JacksonHelper
import okhttp3.OkHttpClient
import org.springframework.beans.factory.ObjectProvider
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.cloud.commons.httpclient.DefaultOkHttpClientConnectionPoolFactory
import org.springframework.cloud.commons.httpclient.OkHttpClientConnectionPoolFactory
import org.springframework.cloud.commons.httpclient.OkHttpClientFactory
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory
import org.springframework.http.converter.StringHttpMessageConverter
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.web.client.RestTemplate
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit

open class MultiRestTemplates(
    private val httpClientFactory: OkHttpClientFactory,
    private val restTemplateBuilder: RestTemplateBuilder,
    private val okHttpClientProperties: OkHttpClientProperties = OkHttpClientProperties(),
    private val poolFactory: OkHttpClientConnectionPoolFactory = DefaultOkHttpClientConnectionPoolFactory(),
    private val customizers: Collection<IOkHttpClientCustomizer>? = null,
    private val defaultRestTemplate: RestTemplate? = null
) {
    private val clients = ConcurrentHashMap<String, RestTemplate>()

    init {
        createOkHttpClient(null, null)
    }

    protected val defaultTemplate: RestTemplate by lazy {
        defaultRestTemplate ?: createRestTemplate(null, null)
    }

    open fun getRestTemplate(pfxFile: String?, password: String?): RestTemplate {
        if (pfxFile.isNullOrBlank()) {
            return defaultTemplate
        }
        return createRestTemplate(pfxFile, password)
    }

    private fun createRestTemplate(
        pfxFile: String?,
        password: String?
    ): RestTemplate {
        return clients.getOrPut(pfxFile ?: "___no_cert") {
            val client = createOkHttpClient(pfxFile, password)
            restTemplateBuilder.build().apply {
                this.requestFactory = OkHttp3ClientHttpRequestFactory(client)
//                this.messageConverters.add(0, MappingJackson2HttpMessageConverter(JacksonHelper.defaultObjectMapper))
//                this.messageConverters.add(1, StringHttpMessageConverter(Charsets.UTF_8))
//
//                this.messageConverters.filterIsInstance<StringHttpMessageConverter>().forEach {
//                    it.setWriteAcceptCharset(false)
//                }
            }
        }
    }

    protected fun createOkHttpClient(pfx12File: String?, password: String?): OkHttpClient {
        val disableSslValidation: Boolean = okHttpClientProperties.sslValidationDisabled

        val maxTotalConnections: Int = okHttpClientProperties.maxConnections
        val timeToLive: Long = okHttpClientProperties.timeToLive.toMillis()
        val pool = poolFactory.create(maxTotalConnections, timeToLive, TimeUnit.MILLISECONDS)

        val sslSocketFactory =
            if (pfx12File.isNullOrBlank()) null else SSLUtilities.createSSLSocketFactory(pfx12File, password)

        return httpClientFactory.createBuilder(disableSslValidation)
            .connectTimeout(okHttpClientProperties.connectTimeout)
            .readTimeout(okHttpClientProperties.readTimeout)
            .writeTimeout(okHttpClientProperties.writeTimeout)
            .followRedirects(okHttpClientProperties.followRedirects)
            .connectionPool(pool)
            .addInterceptor(OkHttpLoggingInterceptor())
            .let { builder ->
                if (!customizers.isNullOrEmpty()) {
                    customizers.forEach {
                        it.customize(builder)
                    }
                }
                if (sslSocketFactory != null) {
                    builder.sslSocketFactory(sslSocketFactory, SSLUtilities.NoneValidationTrustManager)
                } else {
                    builder
                }
            }

            .build()
    }
}