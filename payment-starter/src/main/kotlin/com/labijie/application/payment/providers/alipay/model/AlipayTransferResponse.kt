package com.labijie.application.payment.providers.alipay.model

import com.labijie.application.thridparty.alipay.AlipayResponseBase
import org.codehaus.jackson.annotate.JsonProperty

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