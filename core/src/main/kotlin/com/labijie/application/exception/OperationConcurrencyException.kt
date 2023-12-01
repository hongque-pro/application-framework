package com.labijie.application.exception

import com.labijie.application.ApplicationErrors
import com.labijie.application.ErrorCodedException

/**
 * @author Anders Xiao
 * @date 2023-11-29
 */
class OperationConcurrencyException (message:String? = null)
    : ErrorCodedException(ApplicationErrors.DataOperationConcurrency, message?: "operation concurrency, please retry again.") {
}