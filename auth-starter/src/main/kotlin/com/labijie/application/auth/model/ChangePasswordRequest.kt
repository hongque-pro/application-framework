package com.labijie.application.auth.model

import jakarta.validation.constraints.NotBlank

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-10-18
 */
data class ChangePasswordRequest(
    var oldPassword:String? = null,
    @get:NotBlank
    var newPassword:String = ""
)