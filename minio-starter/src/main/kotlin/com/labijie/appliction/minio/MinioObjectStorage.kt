package com.labijie.appliction.minio

import com.labijie.application.BucketPolicy
import com.labijie.application.component.IObjectStorage
import com.labijie.appliction.minio.configuration.MinioProperties
import com.labijie.infra.utils.logger
import io.minio.*
import io.minio.errors.ErrorResponseException
import io.minio.http.Method
import org.slf4j.LoggerFactory
import java.io.FileInputStream
import java.io.InputStream
import java.net.URL
import java.util.concurrent.TimeUnit

class MinioObjectStorage(
    private val applicationName: String,
    private val properties: MinioProperties,
    private val minioClient: MinioClient
) : IObjectStorage {

    companion object{
        internal val logger = LoggerFactory.getLogger(MinioObjectStorage::class.java)
        const val MIN_PART_SIZE =  5 *1024L * 1024L
    }

    init {
        if ((properties.publicBucket.isBlank() || properties.privateBucket.isBlank()) && applicationName.isBlank()) {
            throw IllegalArgumentException("When public bucket or private bucket use blank name, application name can not be blank.")
        }
    }

    fun getBucket(bucketPolicy: BucketPolicy): String {
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
        } catch (ex: ErrorResponseException) {
            if(ex.isNoSuchKey || ex.IsNoSuchBucketError){
                return false
            }
            throw ex
        }
    }

    override fun deleteObject(key: String, bucketPolicy: BucketPolicy): Boolean {
        val bucket = getBucket(bucketPolicy)
        try {
            minioClient.removeObject(
                RemoveObjectArgs
                    .builder()
                    .bucket(bucket)
                    .`object`(key)
                    .build()
            )
            return true
        } catch (ex: Exception) {
            when (ex) {
                is ErrorResponseException-> {
                    if(ex.errorResponse().code() == MinioErrorCodes.NoSuchBucket) {
                        logger.warn("Bucket '$bucket' is not found when delete object '$key'.")
                        return false
                    }
                }
            }
            throw ex
        }
    }

    override fun generateObjectUrl(key: String, bucketPolicy: BucketPolicy): URL {
        if(bucketPolicy == BucketPolicy.PUBLIC){
            return URL(properties.baseUrl(), "${getBucket(bucketPolicy)}/$key")
        }
        return presignUrl(bucketPolicy, key, Method.GET)
    }

    fun presignUrl(bucketPolicy: BucketPolicy, key: String, method: Method): URL {
        val url = minioClient.getPresignedObjectUrl(
            GetPresignedObjectUrlArgs
                .builder()
                .method(method)
                .bucket(getBucket(bucketPolicy))
                .`object`(key)
                .expiry(properties.preSignedDuration.seconds.toInt(), TimeUnit.SECONDS)
                .build()
        )
        return URL(url)
    }

    override fun uploadObject(key: String, stream: InputStream, bucketPolicy: BucketPolicy, contentLength: Long?) {
        var size = -1L
        var partSize = MIN_PART_SIZE//5M ~ 5G

        if (contentLength != null) {
            size = contentLength
            partSize = -1
        }

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