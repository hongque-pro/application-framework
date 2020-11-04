package com.labijie.application.auth.social.providers.alipay.model

import com.fasterxml.jackson.annotation.JsonProperty

/**
 *
 * @author lishiwen
 * @date 19-12-28
 * @since JDK1.8
 */
class ErrorResponse {
    var code: String? = null

    var msg: String? = null

    @get:JsonProperty("sub_code")
    var subCode: String? = null

    @get:JsonProperty("sub_msg")
    var subMsg: String? = null
}