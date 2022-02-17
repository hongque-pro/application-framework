package com.labijie.application.open

import com.labijie.application.SpringContext
import com.labijie.application.configuration.OpenApiClientProperties
import com.labijie.application.open.RestTemplateExtensions.configureForOpenApi
import com.labijie.application.open.internal.OpenApiClientHttpRequestInterceptor
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.web.client.RestTemplate
import java.lang.IllegalArgumentException
import java.util.concurrent.ConcurrentHashMap

/**
 *
 * @Author: Anders Xiao
 * @Date: 2022/2/17
 * @Description:
 */
object RestTemplateExtensions {
    private val templates = ConcurrentHashMap<String, RestTemplate>()

    fun RestTemplate.configureForOpenApi(clientProperties: OpenApiClientProperties? = null, builder: RestTemplateBuilder? = null): RestTemplate {
        val properties = clientProperties ?:  SpringContext.current.getBean(OpenApiClientProperties::class.java)
        val restTemplateBuilder = builder ?: SpringContext.current.getBean(RestTemplateBuilder::class.java)

        if(properties.appid.isBlank()){
            throw IllegalArgumentException("appid for open api client can not be null or empty string.")
        }

        return templates.computeIfAbsent(properties.appid) {
            restTemplateBuilder.build().apply {
                val interceptor = OpenApiClientHttpRequestInterceptor(properties)
                this.interceptors.add(interceptor)
            }
        }
    }
}