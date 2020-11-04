package com.labijie.application.web.handler

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-09-05
 */
open class ErrorResponse(
    var error:String,
    @get:JsonProperty("error_description")
    var errorDescription:String?,

    @get:JsonProperty("error_details")
    var details: Map<String, Any>? = null)