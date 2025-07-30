package com.labijie.application.email.configuration

import org.springframework.boot.context.properties.ConfigurationProperties
import java.time.Duration

/**
 * @author Anders Xiao
 * @date 2025/7/29
 */
@ConfigurationProperties("application.email")
class EmailServiceProperties {
    var endpointEnabled: Boolean = true
    var mainProvider: String = ""
    var sendRateLimit: Duration = Duration.ofMinutes(1)
}