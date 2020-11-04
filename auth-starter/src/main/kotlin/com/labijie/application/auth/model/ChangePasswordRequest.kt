package com.labijie.application.auth.model

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-10-18
 */
data class ChangePasswordRequest(
    var oldPassword:String,
    var newPassword:String
)