package com.labijie.application.web.client

import com.labijie.application.configuration.HttpClientProperties
import com.labijie.application.httpclient.NettyUtils
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.http.client.ReactorNettyClientRequestFactory
import org.springframework.web.client.RestTemplate
import java.util.concurrent.ConcurrentHashMap

open class MultiRestTemplates(
    private val restTemplateBuilder: RestTemplateBuilder,
    private val httpClientProperties: HttpClientProperties,
    private val defaultRestTemplate: RestTemplate? = null
) {
    private val clients = ConcurrentHashMap<String, RestTemplate>()


    private val defaultTemplate: RestTemplate by lazy {
        defaultRestTemplate ?: createRestTemplate(null, null)
    }

    open fun getRestTemplate(pfxFile: String? = null, password: String? = null): RestTemplate {
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
            restTemplateBuilder.build().apply {
                val client = NettyUtils.createHttpClient(
                    httpClientProperties.connectTimeout,
                    httpClientProperties.readTimeout,
                    httpClientProperties.writeTimeout,
                    pfxFile,
                    password,
                    httpClientProperties.loggerEnabled
                )
                client.secure()
                val factory = ReactorNettyClientRequestFactory(client)
                this.requestFactory = factory
            }
        }
    }
}