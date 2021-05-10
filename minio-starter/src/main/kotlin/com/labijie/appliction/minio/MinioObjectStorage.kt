package com.labijie.appliction.minio

import com.labijie.application.BucketPolicy
import com.labijie.application.component.IObjectStorage
import com.labijie.appliction.minio.configuration.MinioProperties
import io.minio.*
import io.minio.http.Method
import java.io.FileInputStream
import java.io.InputStream
import java.net.URL
import java.security.InvalidKeyException
import java.util.concurrent.TimeUnit

class MinioObjectStorage(
    private val properties: MinioProperties,
    private val minioClient: MinioClient
) : IObjectStorage {

    private val basUrl = if (properties.domainUrl.isBlank()) properties.endpoint else URL(properties.domainUrl.trim())

    private fun getBucket(bucketPolicy: BucketPolicy): String {
        return if (bucketPolicy == BucketPolicy.PUBLIC) {
            this.properties.publicBucket
        } else {
            this.properties.privateBucket
        }
    }

    override fun existObject(key: String, throwIfNotExisted: Boolean, bucketPolicy: BucketPolicy): Boolean {
        if (key.isBlank()) {
            return false
        }
        return try {
            val state = minioClient.statObject(
                StatObjectArgs
                    .builder()
                    .bucket(getBucket(bucketPolicy))
                    .`object`(key)
                    .build()
            )
            state != null
        } catch (e: InvalidKeyException) {
            false
        }
    }

    override fun deleteObject(key: String, bucketPolicy: BucketPolicy): Boolean {
        return try {
            minioClient.removeObject(
                RemoveObjectArgs
                    .builder()
                    .bucket(getBucket(bucketPolicy))
                    .`object`(key)
                    .build()
            )
            true
        } catch (e: InvalidKeyException) {
            false
        }
    }

    override fun generateObjectUrl(key: String, bucketPolicy: BucketPolicy): URL {
        return if (bucketPolicy == BucketPolicy.PUBLIC) {
            URL(this.basUrl, "${getBucket(bucketPolicy)}/$key")
        } else {
            val url = minioClient.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs
                    .builder()
                    .method(Method.PUT)
                    .bucket(getBucket(bucketPolicy))
                    .`object`(key)
                    .expiry(properties.presignedExpiration.seconds.toInt(), TimeUnit.SECONDS)
                    .build()
            )
            return URL(url)
        }
    }

    override fun uploadObject(key: String, stream: InputStream, bucketPolicy: BucketPolicy, contentLength: Long?) {
        var size = -1L
        var partSize = 120 * 1024L //5M ~ 5G

        if (contentLength != null) {
            size = contentLength
            partSize = -1
        }

        FileInputStream("")
        val args = PutObjectArgs.builder()
            .bucket(getBucket(bucketPolicy))
            .`object`(key)
            .stream(stream, size, partSize)
            .build()

        minioClient.putObject(args)
    }

    override fun getObject(key: String, bucketPolicy: BucketPolicy): ByteArray {
        val stream = minioClient.getObject(
            GetObjectArgs.builder()
                .bucket(getBucket(bucketPolicy))
                .`object`(key)
                .build()
        )

        stream.use {
            return it.readBytes()
        }
    }
}