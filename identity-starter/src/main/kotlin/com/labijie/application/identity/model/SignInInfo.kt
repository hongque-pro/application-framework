package com.labijie.application.identity.model

import jakarta.validation.constraints.NotBlank

/**
 * @author Anders Xiao
 * @date 2025/7/31
 */
class SignInInfo {
    @NotBlank
    var user: String = ""

    var password: String? = null
}