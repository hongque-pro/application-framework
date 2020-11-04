package com.labijie.application.payment.providers.alipay.model

import com.fasterxml.jackson.annotation.JsonProperty
import com.labijie.application.thridparty.alipay.AlipayResponseBase

class AlipayTradeQueryReponse: AlipayResponseBase() {
    @JsonProperty("out_trade_no")
    var outTradeNo:String = ""

    @JsonProperty("trade_no")
    val tradeNo:String = ""

    @JsonProperty("trade_status")
    var tradeStatus:String = ""

    @JsonProperty("total_amount")
    var totalAmount:String = ""

    @JsonProperty("buyer_logon_id")
    var buyerLogonId:String = ""

    @JsonProperty("buyer_user_id")
    var buyerUserId:String = ""
}