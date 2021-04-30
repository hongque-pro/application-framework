package com.labijie.application.identity.exception

import com.labijie.application.ErrorCodedException
import com.labijie.application.identity.IdentityErrors

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-09-11
 */
class InvalidRepeatPasswordException(message:String? = null): ErrorCodedException(
    IdentityErrors.INVALID_REPEAT_PWD,
    message ?: "Repeat password and password unmatched."
) {
}