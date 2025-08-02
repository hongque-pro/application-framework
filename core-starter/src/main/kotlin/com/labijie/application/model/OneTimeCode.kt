package com.labijie.application.model

import java.time.Duration


/**
 * @author Anders Xiao
 * @date 2025/7/31
 */
data class OneTimeCode(
    val code: String = "",
    val stamp: String = "",
    var expiration: Duration = Duration.ZERO,
)