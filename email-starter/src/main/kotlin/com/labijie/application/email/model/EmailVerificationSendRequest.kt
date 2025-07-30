package com.labijie.application.email.model

import com.labijie.application.model.VerificationCodeType
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

/**
 * @author Anders Xiao
 * @date 2025/7/30
 */
class EmailVerificationSendRequest {
    @NotBlank
    @Email
    var to: String = ""

    var type: VerificationCodeType = VerificationCodeType.General
}