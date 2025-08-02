package com.labijie.application.exception

import com.labijie.application.ApplicationErrors
import com.labijie.application.ErrorCodedException

/**
 * @author Anders Xiao
 * @date 2025/7/31
 */
class UnsupportedBusinessOperation(reason: String? = null, message: String? = null, cause: Throwable? = null) :
    ErrorCodedException(ApplicationErrors.UnsupportedOperation, message, cause) {

    init {
        if(!reason.isNullOrBlank()) {
            this.args.putIfAbsent("reason", reason)
        }
    }
}