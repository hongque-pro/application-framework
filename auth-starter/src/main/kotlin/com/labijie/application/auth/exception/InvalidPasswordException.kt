package com.labijie.application.auth.exception

import com.labijie.application.ErrorCodedException
import com.labijie.application.auth.AuthErrors

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-09-08
 */
class InvalidPasswordException(message:String? = null) : ErrorCodedException(
    AuthErrors.INVALID_PASSWORD,
    message ?: "The password was incorrect."
) {
}