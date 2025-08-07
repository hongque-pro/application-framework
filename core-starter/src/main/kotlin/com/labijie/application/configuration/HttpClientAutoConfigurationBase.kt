/**
 * @author Anders Xiao
 * @date 2024-02-02
 */
package com.labijie.application.configuration

import com.labijie.infra.json.JacksonHelper
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.http.converter.AbstractHttpMessageConverter
import org.springframework.http.converter.ByteArrayHttpMessageConverter
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.http.converter.StringHttpMessageConverter
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter


abstract class HttpClientAutoConfigurationBase {

    @Bean
    @ConfigurationProperties("application.httpclient")
    @ConditionalOnMissingBean(HttpClientProperties::class)
    fun httpclientProperties(): HttpClientProperties {
        return HttpClientProperties()
    }

    protected fun MutableList<HttpMessageConverter<*>>.applyDefaultConverter() {

        val jsonMessageConverter = MappingJackson2HttpMessageConverter(JacksonHelper.defaultObjectMapper)

        val index = this.indexOfLast { it is MappingJackson2HttpMessageConverter }

        if (index >= 0) {
            this.removeIf {
                it is MappingJackson2HttpMessageConverter
            }
            this.add(index, jsonMessageConverter)
        } else {
            this.add(jsonMessageConverter)
        }

        //修正 UTF-8 编码
        this.filterIsInstance<AbstractHttpMessageConverter<*>>().forEach {
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