package com.labijie.application.payment.providers.alipay.model

import com.fasterxml.jackson.annotation.JsonProperty

data class AlipayPayeeInfo(
    var identity: String,
    @JsonProperty("identity_type")
    var identityType: String = "ALIPAY_USER_ID ",
    var name:String? = null
)