package com.labijie.appliction.minio.configuration

import com.labijie.infra.utils.ifNullOrBlank
import org.springframework.boot.context.properties.ConfigurationProperties
import java.net.URL
import java.time.Duration


@ConfigurationProperties("application.minio")
data class MinioProperties(
    var endpoint: URL = URL("http://localhost:9000"),
    var domainUrl: URL? = null,
    var region: String = "us-east-1",
    var privateBucket: String = "",
    var publicBucket: String = "",
    var accessKey: String = "",
    var secretKey: String = "",
    var controllerEnabled: Boolean = true,
    /**
     * presigned object url timeout.
     */
    var preSignedDuration: Duration = Duration.ofMinutes(10),
    /**
     * sts (assumeRole) token timeout.
     */
    var stsTokenDuration: Duration = Duration.ofMinutes(10)
) {
    fun safeStsTokenDurationInSeconds(): Int {
        return this.stsTokenDuration.seconds.coerceAtMost(604800L).coerceAtLeast(900L).toInt()
    }

    fun baseUrl() = this.domainUrl ?: this.endpoint

    fun safePrivateBucket(applicationName: String): String {
        return privateBucket.ifNullOrBlank { "$applicationName-private" }
    }

    fun safePublicBucket(applicationName: String): String {
        return publicBucket.ifNullOrBlank { "$applicationName-public" }
    }
}