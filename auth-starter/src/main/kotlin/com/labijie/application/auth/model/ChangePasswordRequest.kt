package com.labijie.application.auth.model

import javax.validation.constraints.NotBlank

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-10-18
 */
data class ChangePasswordRequest(
    @get:NotBlank
    var oldPassword:String,
    @get:NotBlank
    var newPassword:String
)