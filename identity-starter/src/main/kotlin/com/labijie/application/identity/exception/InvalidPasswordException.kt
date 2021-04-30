package com.labijie.application.identity.exception

import com.labijie.application.ErrorCodedException
import com.labijie.application.identity.IdentityErrors

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-09-08
 */
class InvalidPasswordException(message:String? = null) : ErrorCodedException(
    IdentityErrors.INVALID_PASSWORD,
    message ?: "The password was incorrect."
) {
}