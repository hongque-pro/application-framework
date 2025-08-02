package com.labijie.application.auth.model

import jakarta.validation.constraints.NotNull

/**
 * @author Anders Xiao
 * @date 2025/7/31
 */
class ChangeEmailRequest {
    @get:NotNull
    var newEmail: VerifiedEmail = VerifiedEmail()

    var password: String? = null
}