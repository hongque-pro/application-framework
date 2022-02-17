package com.labijie.application.configuration

import org.springframework.boot.context.properties.ConfigurationProperties

/**
 *
 * @Author: Anders Xiao
 * @Date: 2022/2/17
 * @Description:
 */
@ConfigurationProperties("application.open-api.client")
class OpenApiClientProperties {
    var appid: String = ""
    var secret: String = ""
    var algorithm: String = "sha256"
}