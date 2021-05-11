package com.labijie.appliction.minio

import com.labijie.application.BucketPolicy
import com.labijie.application.component.IObjectStorage
import com.labijie.appliction.minio.configuration.MinioProperties
import io.minio.*
import io.minio.http.Method
import java.io.FileInputStream
import java.io.InputStream
import java.lang.IllegalArgumentException
import java.net.URL
import java.security.InvalidKeyException
import java.util.concurrent.TimeUnit

class MinioObjectStorage(
    private val applicationName: String,
    private val properties: MinioProperties,
    private val minioClient: MinioClient
) : IObjectStorage {

    init {
        if((properties.publicBucket.isBlank() || properties.privateBucket.isBlank()) && applicationName.isBlank()){
            throw IllegalArgumentException("When public bucket or private bucket use blank name, application name can not be blank.")
        }
    }

    private fun getBucket(bucketPolicy: BucketPolicy): String {
        return if (bucketPolicy == BucketPolicy.PUBLIC) {
            this.properties.safePublicBucket(applicationName)
        } else {
            this.properties.safePrivateBucket(applicationName)
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
            URL(properties.baseUrl(), "${getBucket(bucketPolicy)}/$key")
        } else {
            val url = minioClient.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs
                    .builder()
                    .method(Method.PUT)
                    .bucket(getBucket(bucketPolicy))
                    .`object`(key)
                    .expiry(properties.preSignedDuration.seconds.toInt(), TimeUnit.SECONDS)
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