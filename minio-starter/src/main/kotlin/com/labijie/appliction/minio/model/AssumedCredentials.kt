package com.labijie.appliction.minio.model

import io.minio.credentials.Credentials
import io.minio.messages.ResponseDate
import java.time.Duration
import javax.crypto.SecretKey

data class AssumedCredentials(
    val accessKey: String,
    val secretKey: String,
    val expiration: Long,
    val sessionToken: String
) {
    companion object {
        fun fromCredentials(credentials: Credentials): AssumedCredentials {
            val expiration = credentials::class.java.fields.first {
                it.name == "expiration"
            }
            expiration.isAccessible = true
            val responseDate = expiration.get(null) as? ResponseDate

            val timeout = responseDate?.zonedDateTime()?.toInstant()?.toEpochMilli()
                ?: System.currentTimeMillis() + Duration.ofDays(30).toMillis()

            return AssumedCredentials(
                credentials.accessKey(),
                credentials.secretKey(),
                timeout,
                credentials.sessionToken()
            )
        }
    }
}