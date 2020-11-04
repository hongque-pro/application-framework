package com.labijie.application.auth.exception

import com.labijie.application.ErrorCodedException
import com.labijie.application.auth.AuthErrors

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-09-11
 */
class InvalidRepeatPasswordException(message:String? = null): ErrorCodedException(
    AuthErrors.INVALID_REPEAT_PWD,
    message ?: "Repeat password and password unmatched."
) {
}