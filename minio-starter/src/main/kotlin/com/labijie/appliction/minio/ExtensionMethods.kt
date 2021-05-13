package com.labijie.appliction.minio

import com.labijie.application.BucketPolicy
import com.labijie.appliction.minio.model.S3Policy
import io.minio.*
import io.minio.errors.ErrorResponseException

fun MinioClient.removeBucketIfExisted(bucketName: String): Boolean {
    try {
        this.removeBucket(RemoveBucketArgs.builder().bucket(bucketName).build())
        return true
    } catch (e: ErrorResponseException) {
        if (e.errorResponse().code() == MinioErrorCodes.NoSuchBucket) {
            return false
        }
        throw e
    }
}

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
            if(policy == BucketPolicy.PUBLIC) {
                val policyArgs = SetBucketPolicyArgs
                    .builder()
                    .bucket(bucketName)
                    .config(S3Policy.makePublic(bucketName).toString())
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