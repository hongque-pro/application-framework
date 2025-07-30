package com.labijie.application.exception

import com.labijie.application.ApplicationErrors
import com.labijie.application.ErrorCodedException

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-09-12
 */
class OutOfRateLimitationException(message: String? = null, operationName: String? = null, cause: Throwable? = null) :
    ErrorCodedException(ApplicationErrors.OperationOutOfRateLimit, message, cause) {

    init {
        if(!operationName.isNullOrBlank()) {
            this.args.putIfAbsent("operation", operationName)
        }
    }
}