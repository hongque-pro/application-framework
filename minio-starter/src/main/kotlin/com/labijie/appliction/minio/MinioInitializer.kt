package com.labijie.appliction.minio

import com.labijie.application.BucketPolicy
import com.labijie.application.IModuleInitializer
import com.labijie.appliction.minio.configuration.MinioProperties
import com.labijie.infra.spring.configuration.getApplicationName
import io.minio.MinioClient
import org.slf4j.LoggerFactory
import org.springframework.core.env.Environment
import kotlin.system.exitProcess

class MinioInitializer : IModuleInitializer {

    companion object {
        private val logger = LoggerFactory.getLogger(MinioInitializer::class.java)
    }

    fun initialize(environment: Environment, minioProperties: MinioProperties, minioClient: MinioClient) {
        val applicationName = environment.getApplicationName(true)

        val privateBucket = minioProperties.safePrivateBucket(applicationName)
        val publicBucket = minioProperties.safePublicBucket(applicationName)

        try {

            val privateCreated = minioClient.makeBucketIfNotExisted(privateBucket, BucketPolicy.PRIVATE)
            val publicCreated = minioClient.makeBucketIfNotExisted(publicBucket, BucketPolicy.PUBLIC)
            val sb = StringBuilder()
            sb.appendBucketCheckState(privateBucket, privateCreated, BucketPolicy.PRIVATE)
            sb.appendBucketCheckState(publicBucket, publicCreated, BucketPolicy.PUBLIC)

            logger.info(sb.toString())

        } catch (e: Exception) {
            logger.error("Minio bucket check fault.", e)
            exitProcess(-8989)
        }
    }

    private fun StringBuilder.appendBucketCheckState(bucket: String, created: Boolean, policy: BucketPolicy) {
        val policyName = if (policy == BucketPolicy.PRIVATE) "private" else "public"
        if (created) {
            this.appendLine("Minio $policy bucket '$bucket' has been created.")
        } else {
            this.appendLine("Minio $policy bucket '$bucket' already existed, skip creation step.")
        }
    }
}