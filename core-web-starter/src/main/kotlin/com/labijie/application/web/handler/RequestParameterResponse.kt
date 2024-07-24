package com.labijie.application.web.handler

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-09-13
 */
class RequestParameterResponse(
    error: String,
    parameterName: String?,
    description: String? = null
) : ErrorResponse(error = error, errorDescription = description) {

    @Suppress("unused")
    val param: String? = parameterName?.let { it.ifBlank { null } }
}