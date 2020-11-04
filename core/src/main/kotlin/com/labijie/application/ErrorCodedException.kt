package com.labijie.application

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-09-05
 */
open class ErrorCodedException(
    val error: String,
    message: String,
    cause: Throwable? = null
) : ApplicationRuntimeException(message, cause){
    open fun getDetails():Map<String, Any>? {
        return null
    }
}