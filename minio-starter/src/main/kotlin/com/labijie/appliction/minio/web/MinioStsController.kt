package com.labijie.appliction.minio.web

import com.labijie.application.BucketPolicy
import com.labijie.application.MimeUtils
import com.labijie.application.model.SimpleValue
import com.labijie.appliction.minio.MinioObjectStorage
import com.labijie.appliction.minio.MinioUtils
import com.labijie.appliction.minio.PresignedUrl
import com.labijie.appliction.minio.configuration.MinioProperties
import com.labijie.appliction.minio.model.AssumedCredentials
import com.labijie.infra.spring.configuration.getApplicationName
import io.minio.GetPresignedObjectUrlArgs
import io.minio.MinioClient
import io.minio.http.Method
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.env.Environment
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import javax.validation.constraints.NotBlank
import kotlin.io.path.Path
import kotlin.io.path.extension


@RestController
@RequestMapping("/objects")
@Validated
class MinioStsController {
    @Autowired
    private lateinit var environment: Environment

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
            this.privateBucket = properties.safePrivateBucket(environment.getApplicationName())
            this.publicBucket = properties.safePublicBucket(environment.getApplicationName())
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
    fun preSignUrl(@NotBlank @RequestParam("key") key: String): PresignedUrl {
        val url = minioObjectStorage.generateObjectUrl(key)
        val ext = key.substringAfterLast('.', "")
        val contentType = MimeUtils.getMimeByExtensions(ext)
        return PresignedUrl(url.toString(), properties.preSignedDuration.seconds.toInt(), contentType)
    }

    /**
     * 获取私有存储桶（Bucket）临时访问路径
     */
    @PostMapping("/pre-sign-upload")
    fun preSignUpload(@NotBlank @RequestParam("key") key: String,
                      @RequestParam("b") bucket: BucketPolicy): PresignedUrl {
        val url = minioObjectStorage.presignUrl(bucket, key, Method.PUT)
        val ext = key.substringAfterLast('.', "")
        val contentType = MimeUtils.getMimeByExtensions(ext)
        return PresignedUrl(url.toString(), properties.preSignedDuration.seconds.toInt(), contentType)
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