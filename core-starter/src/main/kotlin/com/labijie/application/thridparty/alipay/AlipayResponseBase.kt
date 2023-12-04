package com.labijie.application.thridparty.alipay

import com.fasterxml.jackson.annotation.JsonProperty

open class AlipayResponseBase {
    var code: String? = null

    var msg: String? = null

    @get:JsonProperty("sub_code")
    var subCode: String? = null

    @get:JsonProperty("sub_msg")
    var subMsg: String? = null
}