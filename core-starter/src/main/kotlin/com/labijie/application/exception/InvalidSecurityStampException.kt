package com.labijie.application.exception

import com.labijie.application.ApplicationErrors
import com.labijie.application.ErrorCodedException

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-10-19
 */
class InvalidSecurityStampException (message:String? = null)
    : ErrorCodedException(ApplicationErrors.InvalidSecurityStamp, message) {
}