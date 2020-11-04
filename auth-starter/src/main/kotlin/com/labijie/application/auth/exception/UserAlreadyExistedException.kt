package com.labijie.application.auth.exception

import com.labijie.application.ErrorCodedException
import com.labijie.application.auth.AuthErrors

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-09-13
 */
class UserAlreadyExistedException(message:String? = null) : ErrorCodedException(AuthErrors.USER_ALREADY_EXISTED, message ?: "user already existed") {
}