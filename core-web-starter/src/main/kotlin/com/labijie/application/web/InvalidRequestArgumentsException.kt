package com.labijie.application.web

import com.labijie.application.ApplicationErrors
import com.labijie.application.ErrorCodedException

/**
 * @author Anders Xiao
 * @date 2025/7/27
 */
class InvalidRequestArgumentsException(fieldName: String?, errorMessage: String? = null) : ErrorCodedException(ApplicationErrors.BadRequestParameter, errorMessage ?: "Invalid arguments in request") {
    init {
        fieldName?.let {
            args.putIfAbsent(fieldName, errorMessage ?: "Invalid value for $fieldName.")
        }
    }
}