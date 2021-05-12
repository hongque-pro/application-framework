package com.labijie.appliction.minio.web

import com.labijie.application.model.SimpleValue
import com.labijie.appliction.minio.MinioObjectStorage
import com.labijie.appliction.minio.MinioUtils
import com.labijie.appliction.minio.configuration.MinioProperties
import com.labijie.appliction.minio.model.AssumedCredentials
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
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
        MinioConfig().apply {
            val baseUrl = properties.baseUrl()
            this.schema = baseUrl.protocol
            this.host = baseUrl.host
            this.port = if(baseUrl.port > 0) baseUrl.port else null
            this.region = properties.region
            this.privateBucket = properties.privateBucket
            this.publicBucket = properties.publicBucket
        }
    }

    @GetMapping("/cnf")
    fun config(): MinioConfig {
        return config
    }

    @PostMapping("/assume-role")
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
        var schema: String = "",
        var host: String = "",
        var port: Int? = null,
        var region: String = "",
        var privateBucket: String = "",
        var publicBucket: String = "",
    )
}