package com.labijie.application.web.handler

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-09-13
 */
class MissingParameterResponse(
    error:String,
    description:String,
    var param:String) : ErrorResponse(error = error, errorDescription = description)