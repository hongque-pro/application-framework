package com.labijie.application.payment.providers.alipay.model

import com.labijie.application.thridparty.alipay.AlipayResponseBase
import org.codehaus.jackson.annotate.JsonProperty

class AlipayTransferQueryResponse : AlipayResponseBase() {
    @JsonProperty("order_id")
    var orderId:String = ""

    @JsonProperty("status")
    var status:String = ""

    @JsonProperty("pay_date")
    var payDate:String = ""
    @JsonProperty("out_biz_no")
    var outBizNo:String = ""
    @JsonProperty("error_code")
    var errorCode:String? = null
    @JsonProperty("fail_reason")
    var failReason:String? = null
}