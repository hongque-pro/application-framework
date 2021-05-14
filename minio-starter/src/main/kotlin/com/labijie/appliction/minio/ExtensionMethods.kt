package com.labijie.appliction.minio

import com.labijie.application.BucketPolicy
import com.labijie.appliction.minio.model.S3Policy
import io.minio.*
import io.minio.errors.ErrorResponseException
import io.minio.messages.DeleteObject


fun MinioClient.makeBucketIfNotExisted(bucketName: String, policy: BucketPolicy): Boolean {
    val found = this.bucketExists(
        BucketExistsArgs
            .builder()
            .bucket(bucketName)
            .build()
    )

    if (!found) {
        try {
            this.makeBucket(
                MakeBucketArgs
                    .builder()
                    .bucket(bucketName)
                    .build()
            )
            //Minio 默认是私有存储桶
            if (policy == BucketPolicy.PUBLIC) {
                val policyArgs = SetBucketPolicyArgs
                    .builder()
                    .bucket(bucketName)
                    .config(S3Policy.makeReadonly(bucketName).toString())
                    .build()
                this.setBucketPolicy(policyArgs)
            }
            return true
        } catch (e: ErrorResponseException) {
            if (e.errorResponse().code() !in arrayOf(
                    MinioErrorCodes.BucketAlreadyOwnedByYou,
                    MinioErrorCodes.BucketAlreadyExists
                )
            ) {
                throw e
            }
        }
    }
    return false
}


fun MinioClient.removeBucketIfExisted(bucket: String, force: Boolean = false): Boolean {
    try {
        if (force) {
            val items = this.listObjects(ListObjectsArgs.builder().bucket(bucket).build())

            val names = mutableSetOf<String>()
            val objects = items.map { item ->
                val name = item.get().objectName()
                names.add(name)
                DeleteObject(name)

            }
            if (objects.isNotEmpty()) {
                val results = this.removeObjects(RemoveObjectsArgs.builder().bucket(bucket).objects(objects).build())
                results.map {
                    it.get()
                }
                MinioObjectStorage.logger.info(
                    "Remove bucket '$bucket' with objects:${System.lineSeparator()}${names.joinToString(System.lineSeparator())}"
                )
            }

        }

        this.removeBucket(RemoveBucketArgs.builder().bucket(bucket).build())
        return true
    } catch (e: ErrorResponseException) {
        if (e.IsNoSuchBucketError) {
            return false
        }
        throw e
    }
}

val ErrorResponseException.IsNoSuchBucketError
    get() = this.errorResponse().code() == MinioErrorCodes.NoSuchBucket

val ErrorResponseException.IsBucketAlreadyExistsError
    get() = this.errorResponse().code() == MinioErrorCodes.BucketAlreadyExists || this.errorResponse()
        .code() == MinioErrorCodes.BucketAlreadyOwnedByYou

val ErrorResponseException.isBucketNotEmptyError
    get() = this.errorResponse().code() == MinioErrorCodes.BucketNotEmpty

val ErrorResponseException.isNoSuchKey
    get() = this.errorResponse().code() == MinioErrorCodes.NoSuchKey