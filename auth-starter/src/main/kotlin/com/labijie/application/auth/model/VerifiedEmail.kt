package com.labijie.application.auth.model

import com.labijie.application.model.OneTimeCodeVerifyRequest
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

/**
 * @author Anders Xiao
 * @date 2025/7/31
 */
data class VerifiedEmail(
    @get: NotBlank
    @get: Email
    var email: String = "",

    @get:NotNull
    var verify: OneTimeCodeVerifyRequest? = null
)