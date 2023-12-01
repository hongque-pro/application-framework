package com.labijie.application.configuration

import org.springframework.boot.context.properties.ConfigurationProperties
import java.time.Duration

/**
 *
 * @author lishiwen
 * @date 20-5-18
 * @since JDK1.8
 */

@ConfigurationProperties("application.sms")
data class SmsBaseSettings(
    var messageExpiration: Duration = Duration.ofMinutes(1),
    var sendRateLimit: Duration = Duration.ofSeconds(30)
)
