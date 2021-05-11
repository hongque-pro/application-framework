package com.labijie.appliction.minio.web

import com.labijie.application.model.SimpleValue
import com.labijie.application.propertiesFrom
import com.labijie.appliction.minio.MinioObjectStorage
import com.labijie.appliction.minio.MinioUtils
import com.labijie.appliction.minio.configuration.MinioProperties
import com.labijie.appliction.minio.model.AssumedCredentials
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import java.net.URL
import javax.validation.constraints.NotBlank


@RestController
@RequestMapping("/objects")
@Validated
class MinioStsController {

    @Autowired
    private lateinit var properties: MinioProperties

    @Autowired
    private lateinit var minioUtils: MinioUtils

    @Autowired
    private lateinit var minioObjectStorage: MinioObjectStorage

    private val config by lazy {
        MinioConfig().propertiesFrom(properties)
    }

    @GetMapping("/cnf")
    fun config(): MinioConfig {
        return config
    }

    @PostMapping("/assumeRole")
    fun assumeRole(): AssumedCredentials = minioUtils.assumeRole()


    /**
     * 获取私有存储桶（Bucket）临时访问路径
     */
    @PostMapping("/pre-sign-url")
    fun preSignUrl(@NotBlank @RequestParam("key") key: String): SimpleValue<String> {
        val url = minioObjectStorage.generateObjectUrl(key)
        return SimpleValue(url.toString())
    }


    data class MinioConfig(
        var domainUrl: String = "",
        var region: String = "",
        var privateBucket: String = "",
        var publicBucket: String = "",
    )
}