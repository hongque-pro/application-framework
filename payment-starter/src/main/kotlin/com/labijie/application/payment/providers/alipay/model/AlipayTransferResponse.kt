package com.labijie.application.payment.providers.alipay.model

import com.fasterxml.jackson.annotation.JsonProperty
import com.labijie.application.thridparty.alipay.AlipayResponseBase

class AlipayTransferResponse : AlipayResponseBase() {
    @JsonProperty("out_biz_no")
    var outBizNo: String = ""

    @JsonProperty("order_id")
    var orderId: String = ""

    @JsonProperty("pay_fund_order_id")
    var payFndOrderId: String = ""

    @JsonProperty("status")
    var status: String = "SUCCESS"

    var transDate: String = ""
}