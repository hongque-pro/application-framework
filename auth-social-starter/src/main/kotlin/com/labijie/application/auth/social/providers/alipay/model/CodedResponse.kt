package com.labijie.application.auth.social.providers.alipay.model

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-12-14
 */
open class CodedResponse {
    @get:JsonProperty("error_response")
    var error: ErrorResponse = ErrorResponse()
}