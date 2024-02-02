package com.labijie.application.configuration

import org.springframework.boot.context.properties.ConfigurationProperties
import java.time.Duration

/**
 * @author Anders Xiao
 * @date 2023-11-28
 */

class HttpClientProperties {
    var connectTimeout: Duration = Duration.ofSeconds(2)
    var readTimeout: Duration = Duration.ofSeconds(5)
    var writeTimeout: Duration = Duration.ofSeconds(5)
    var loggerEnabled: Boolean = false
}