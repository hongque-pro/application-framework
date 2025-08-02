package com.labijie.application.model

import com.fasterxml.jackson.annotation.JsonProperty
import com.labijie.application.web.interceptor.OneTimeCodeInterceptor
import jakarta.validation.constraints.NotBlank

/**
 * @author Anders Xiao
 * @date 2025/7/31
 */
data class OneTimeCodeVerifyRequest (
    @get:NotBlank
    @get:JsonProperty(OneTimeCodeInterceptor.CODE_KEY)
    var code: String = "",

    @get:NotBlank
    @get:JsonProperty(OneTimeCodeInterceptor.STAMP_KEY)
    var stamp: String = ""
)