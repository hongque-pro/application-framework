package com.labijie.application.web.handler

import com.fasterxml.jackson.annotation.JsonProperty
import com.labijie.application.localeErrorMessage

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-09-05
 */
open class ErrorResponse {
    constructor(
        error:String,
        errorDescription:String? = null,
        details: Map<String, Any>? = null)  {

        this.error = error
        this.errorDescription = errorDescription ?: localeErrorMessage(error)
        this.details = details
    }

    constructor()

    var error:String = ""

    @get:JsonProperty("error_details")
    var details: Map<String, Any>? = null

    @get:JsonProperty("error_description")
    var errorDescription: String = ""
}