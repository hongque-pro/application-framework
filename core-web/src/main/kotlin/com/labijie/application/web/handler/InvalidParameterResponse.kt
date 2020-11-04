package com.labijie.application.web.handler

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-09-13
 */
class InvalidParameterResponse(
    error:String,
    description:String,
    @get:JsonProperty("param_errors")
    var paramErrors:Map<String, String>) :
    ErrorResponse(error = error, errorDescription = description)