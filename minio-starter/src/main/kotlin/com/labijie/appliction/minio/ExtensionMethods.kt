package com.labijie.appliction.minio

import com.labijie.application.BucketPolicy
import com.labijie.appliction.minio.model.S3Policy
import io.minio.BucketExistsArgs
import io.minio.MakeBucketArgs
import io.minio.MinioClient
import io.minio.SetBucketPolicyArgs
import io.minio.errors.ErrorResponseException

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

            val policyArgs = SetBucketPolicyArgs
                .builder()
                .bucket(bucketName)
                .apply {
                    if (policy == BucketPolicy.PUBLIC) {
                        this.config(S3Policy.makePrivate(bucketName).toString())
                    } else {
                        this.config(S3Policy.makePublic(bucketName).toString())
                    }
                }
                .build()
            this.setBucketPolicy(policyArgs)
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