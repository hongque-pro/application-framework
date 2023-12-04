package com.labijie.application.exception

import com.labijie.application.ApplicationErrors
import com.labijie.application.ErrorCodedException

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-09-06
 */
class UserNotFoundException(identity:String, message: String?= null) : ErrorCodedException(
    ApplicationErrors.UserNotFound,
    message?: if(identity.isBlank()) "User was not found" else "User with identity '$identity' was not found") {

    constructor(userId:Long? = null):this(userId?.toString() ?: "")
}