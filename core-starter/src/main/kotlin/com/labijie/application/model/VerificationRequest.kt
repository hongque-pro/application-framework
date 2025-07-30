package com.labijie.application.model

/**
 * @author Anders Xiao
 * @date 2025/7/30
 */
data class VerificationRequest(
    val token: String = "",
    val code: String = ""
)