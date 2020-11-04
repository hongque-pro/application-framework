package com.labijie.application.open.configuration

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties
class OpenApiProperties {
    val jsApiCors: CorsProperties = CorsProperties()
    val pathPattern: String = "/open-api/**"
}