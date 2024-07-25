package com.labijie.application.exception

import com.labijie.application.ApplicationErrors
import com.labijie.application.ErrorCodedException

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-09-06
 */
class UserNotFoundException(identity:String? = null, message: String?= null) : ErrorCodedException(
    ApplicationErrors.UserNotFound,
    message,
    args = identity?.let { mapOf("identity" to it) }
) {

    constructor(userId:Long? = null):this(userId?.toString())
}