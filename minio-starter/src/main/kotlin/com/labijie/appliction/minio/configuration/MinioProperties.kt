package com.labijie.appliction.minio.configuration

import com.fasterxml.jackson.annotation.JsonIgnore
import org.springframework.boot.context.properties.ConfigurationProperties
import java.net.URL
import java.time.Duration


@ConfigurationProperties("application.minio")
data class MinioProperties(
    var endpoint: URL = URL("http://localhost:9000"),
    var domainUrl: String = "",
    var region: String = "",
    var privateBucket: String = "",
    var publicBucket: String = "",
    var accessKey: String = "",
    var secretKey: String = "",
    var stsControllerEnabled: Boolean = true,
    var presignedExpiration: Duration = Duration.ofMinutes(10)
)