package com.labijie.application.payment.car.alipay.model

import com.fasterxml.jackson.annotation.JsonProperty
import com.labijie.application.thridparty.alipay.AlipayResponseBase

/**
 * Author: Anders Xiao
 * Date: Created in 2020/3/17 15:23
 * Copyright: Copyright (c) 2020
 * Github: https://github.com/endink
 */
class AlipayCarTradeQueryReponse : AlipayResponseBase() {
    @JsonProperty("out_biz_trade_no")
    var outBizTradeNo:String = ""

    @JsonProperty("biz_trade_no")
    val bizTradeNo:String = ""

    @JsonProperty("trade_no")
    val tradeNo:String = ""

    @JsonProperty("trade_status")
    var tradeStatus:String = ""

    @JsonProperty("total_fee")
    var totalFee:String = ""

    @JsonProperty("gmt_payment_success")
    var gmtPaymentSuccess:String = ""

    @JsonProperty("buyer_id")
    var buyerId:String = ""
}