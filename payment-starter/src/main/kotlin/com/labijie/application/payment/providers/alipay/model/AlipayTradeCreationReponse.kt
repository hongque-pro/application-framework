package com.labijie.application.payment.providers.alipay.model

import com.fasterxml.jackson.annotation.JsonProperty
import com.labijie.application.thridparty.alipay.AlipayResponseBase

open class AlipayTradeCreationReponse : AlipayResponseBase() {
    @JsonProperty("out_trade_no")
    var outTradeNo:String = ""

    @JsonProperty("trade_no")
    val tradeNo:String = ""
}