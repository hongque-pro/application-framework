package com.labijie.application.auth.model

import com.labijie.application.model.OneTimeCodeVerifyRequest
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

/**
 * @author Anders Xiao
 * @date 2025/7/31
 */
class VerifiedPhone {
    var dialingCode: Short = 86

    @get:NotBlank
    var phoneNumber: String = ""

    @get:NotNull
    var verify: OneTimeCodeVerifyRequest? = null
}