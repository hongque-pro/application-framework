package com.labijie.application.sms.configuration

import org.springframework.boot.context.properties.ConfigurationProperties
import java.time.Duration

/**
 * @author Anders Xiao
 * @date 2025/7/30
 */
@ConfigurationProperties("application.sms")
class SmsServiceProperties {
    var mainProvider: String = ""
    var endpointEnabled: Boolean = true
    var sendRateLimit: Duration = Duration.ofSeconds(30)
}