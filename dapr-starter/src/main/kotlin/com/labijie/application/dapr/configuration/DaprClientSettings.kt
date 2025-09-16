package com.labijie.application.dapr.configuration

import java.time.Duration

/**
 * @author Anders Xiao
 * @date 2025/9/16
 */
data class DaprClientSettings(
    var maxRetries: Int = 1,
    var timeout: Duration = Duration.ofSeconds(5)
)