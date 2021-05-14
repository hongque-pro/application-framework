package com.labijie.appliction.minio

import com.labijie.appliction.minio.configuration.MinioProperties
import com.labijie.appliction.minio.model.AssumedCredentials
import com.labijie.appliction.minio.model.S3Policy
import io.minio.credentials.AssumeRoleProvider
import io.minio.credentials.Provider
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.toRequestBody
import java.lang.IllegalArgumentException


open class MinioUtils(
    private val applicationName: String,
    private val properties: MinioProperties,
    private val okHttpClient: OkHttpClient) {

    init {
        if((properties.publicBucket.isBlank() || properties.privateBucket.isBlank()) && applicationName.isBlank()){
            throw IllegalArgumentException("When public bucket or private bucket use blank name, application name can not be blank.")
        }
    }

    fun assumeRole(): AssumedCredentials {
        val provider = assumeRoleProvider()
        val credential = provider.fetch()
        return AssumedCredentials.fromCredentials(credential, properties.safeStsTokenDurationInSeconds())
    }

    fun assumeRoleProvider(): AssumeRoleProvider {
        val timeout = properties.safeStsTokenDurationInSeconds()

        val policy = S3Policy.makeReadWrite(
            properties.safePrivateBucket(applicationName),
            properties.safePublicBucket(applicationName)
        )


        MinioObjectStorage.logger.info(System.lineSeparator() + policy.toPrettyString())

        return AssumeRoleProvider(
            properties.baseUrl().toString(),
            properties.accessKey,
            properties.secretKey,
            timeout,
            policy.toString(),
            properties.region,
            "arn:aws:s3:::*",
            null,
            null,
            okHttpClient,
        )
    }
}