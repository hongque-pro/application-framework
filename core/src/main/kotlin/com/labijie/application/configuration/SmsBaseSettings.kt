package com.labijie.application.configuration

import org.springframework.boot.context.properties.ConfigurationProperties
import java.time.Duration

/**
 *
 * @author lishiwen
 * @date 20-5-18
 * @since JDK1.8
 */
data class SmsAsyncSettings(
    var sinkEnabled: Boolean = true,
    var messageExpiration: Duration = Duration.ofMinutes(1)
)

@ConfigurationProperties("application.sms")
data class SmsBaseSettings(
    var async: SmsAsyncSettings = SmsAsyncSettings(),
    var sendRateLimit: Duration = Duration.ofSeconds(10)
)
