package com.labijie.application.dummy.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.http.converter.StringHttpMessageConverter
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.client.RestClient
import javax.print.attribute.standard.Media

@RestController
@RequestMapping("/test2")
class Test2Controller(builder: RestClient.Builder) {

    private val restClient: RestClient

    init {
        restClient = builder.messageConverters {
            it.clear()
            it.add(0, StringHttpMessageConverter(Charsets.UTF_8).apply {
                this.supportedMediaTypes = listOf(
                    MediaType.APPLICATION_JSON,
                    MediaType.TEXT_PLAIN
                )
            })
        }.build()
    }

    @GetMapping("/test")
    fun test(): String {
        return "OK"
    }

}