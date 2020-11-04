package com.labijie.application.payment.car.alipay.model

import com.fasterxml.jackson.annotation.JsonProperty
import com.labijie.application.thridparty.alipay.AlipayResponseBase
import jdk.nashorn.internal.objects.annotations.Property

/**
 * Author: Anders Xiao
 * Date: Created in 2020/2/28 13:13
 * Copyright: Copyright (c) 2020
 * Github: https://github.com/endink
 */

/**
 * 支付宝车主平台代扣下单响应
 */
class AlipayCarTradeResponse : AlipayResponseBase() {

    @JsonProperty("user_id")
    var userId:String = ""

    @JsonProperty("trade_no")
    var tradeNo:String = ""

    @JsonProperty("out_trade_no")
    var outTradeNo:String = ""

    @JsonProperty("biz_trade_no")
    var bizTradeNo:String = ""

    @JsonProperty("gmt_payment")
    var gmtPayment:String = ""
}