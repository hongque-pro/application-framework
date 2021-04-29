package com.labijie.application.identity.exception

import com.labijie.application.ErrorCodedException
import com.labijie.application.identity.IdentityErrors

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-09-13
 */
class UserAlreadyExistedException(message:String? = null) : ErrorCodedException(IdentityErrors.USER_ALREADY_EXISTED, message ?: "user already existed") {
}