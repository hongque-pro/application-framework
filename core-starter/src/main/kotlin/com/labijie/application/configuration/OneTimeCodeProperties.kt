package com.labijie.application.configuration

import org.springframework.boot.context.properties.ConfigurationProperties
import java.time.Duration

/**
 * @author Anders Xiao
 * @date 2025/7/30
 */
@ConfigurationProperties("application.one-time-code")
class OneTimeCodeProperties {
    var expiration: Duration = Duration.ofMinutes(5)
}