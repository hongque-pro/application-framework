package com.labijie.appliction.minio

import com.labijie.appliction.minio.configuration.MinioProperties
import com.labijie.appliction.minio.model.AssumedCredentials
import com.labijie.appliction.minio.model.S3Policy
import com.labijie.infra.json.JacksonHelper
import io.minio.credentials.AssumeRoleProvider
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

    private val emptyHttpBody = "".toRequestBody()

    fun assumeRole(): AssumedCredentials {
        val timeout = properties.safeStsTokenDurationInSeconds()

        val assume = AssumeRoleProvider(
            properties.baseUrl().toString(),
            properties.accessKey,
            properties.secretKey,
            timeout,
            JacksonHelper.serializeAsString(S3Policy(properties.safePrivateBucket(applicationName), properties.safePublicBucket(applicationName))),
            properties.region,
            null,
            null,
            null,
            okHttpClient,
        )
        val credential = assume.fetch()
        return AssumedCredentials.fromCredentials(credential, timeout)
    }
}