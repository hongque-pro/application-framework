package com.labijie.appliction.minio.model

import io.minio.credentials.Credentials

data class AssumedCredentials(
    val accessKey: String,
    val secretKey: String,
    val expiration: Int,
    val sessionToken: String
) {
    companion object {
        fun fromCredentials(credentials: Credentials, expireInSeconds: Int): AssumedCredentials {
            //过期时间有时区问题，等待官方改进？
//            val expiration = credentials::class.java.declaredFields.first {
//                it.name == "expiration"
//            }
//            expiration.isAccessible = true
//            val responseDate = expiration.get(credentials) as? ResponseDate
//
//            val timeout = responseDate?.zonedDateTime()?.toInstant()?.toEpochMilli()
//                ?: System.currentTimeMillis() + Duration.ofDays(30).toMillis()

            return AssumedCredentials(
                credentials.accessKey(),
                credentials.secretKey(),
                expireInSeconds,
                credentials.sessionToken()
            )
        }
    }
}