package com.labijie.application.configuration

import org.springframework.boot.context.properties.ConfigurationProperties
import java.time.Duration

/**
 * @author Anders Xiao
 * @date 2025/7/30
 */
@ConfigurationProperties("application.verification-code")
class VerificationCodeProperties {
    var expiration: Duration = Duration.ofMinutes(5)
}