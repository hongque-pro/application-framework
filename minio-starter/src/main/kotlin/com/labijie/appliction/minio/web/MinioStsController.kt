package com.labijie.appliction.minio.web

import com.labijie.application.propertiesFrom
import com.labijie.appliction.minio.configuration.MinioProperties
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/objects")
open class MinioStsController(properties: MinioProperties) {
    private val config = MinioConfig().propertiesFrom(properties)

    @GetMapping("/cnf")
    fun config(): MinioConfig{
        return config
    }

    fun assumeRole():


    data class MinioConfig(var domainUrl: String = "",
                           var region: String = "",
                           var privateBucket: String = "",
                           var publicBucket: String = "",)
}