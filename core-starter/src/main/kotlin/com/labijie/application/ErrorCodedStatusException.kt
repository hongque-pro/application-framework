package com.labijie.application

import org.springframework.http.HttpStatus


/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-12-12
 */
open class ErrorCodedStatusException(
    error: String,
    message: String? = null,
    cause: Throwable? = null,
    status: HttpStatus? = null,
    args: Map<String, String>? = null
) : ErrorCodedException(error, message, cause, args) {

    open val status: HttpStatus = status ?: HttpStatus.CONFLICT
}