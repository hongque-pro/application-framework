package com.labijie.application.payment.providers.wechat.model

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlCData
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.labijie.application.thridparty.wechat.WechatResponse

class WechatRefundQueryResponse : WechatResponse() {

    @JacksonXmlProperty(localName = "refund_id_0")
    @JacksonXmlCData
    var refundId: String = ""

    @JacksonXmlProperty(localName = "out_refund_no_0")
    @JacksonXmlCData
    var outRefundNo: String = ""

    @JacksonXmlProperty(localName = "transaction_id")
    @JacksonXmlCData
    var transactionId: String = ""

    @JacksonXmlProperty(localName = "out_trade_no")
    @JacksonXmlCData
    var outTradeNo: String = ""

    @JacksonXmlProperty(localName = "refund_fee_0")
    @JacksonXmlCData
    var refundFee: Int = 0

    @JacksonXmlProperty(localName = "settlement_refund_fee_0")
    @JacksonXmlCData
    var settlementRefundFee: Int? = null

    @JacksonXmlProperty(localName = "total_fee")
    @JacksonXmlCData
    var totalFee: Int = 0

    @JacksonXmlProperty(localName = "refund_success_time_0")
    @JacksonXmlCData
    var refundSuccessTime: String? = null

    @JacksonXmlProperty(localName = "refund_status_0")
    @JacksonXmlCData
    var refundStatus: String = ""
}