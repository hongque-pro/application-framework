package com.labijie.application.payment.providers.wechat.model

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlCData
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.labijie.application.thridparty.wechat.WechatResponse

class WechatRefundResponse : WechatResponse() {

    @JacksonXmlProperty(localName = "refund_id")
    @JacksonXmlCData
    var refundId: String = ""

    @JacksonXmlProperty(localName = "out_refund_no")
    @JacksonXmlCData
    var outRefundNo:String = ""

    @JacksonXmlProperty(localName = "transaction_id")
    @JacksonXmlCData
    var transactionId: String = ""

    @JacksonXmlProperty(localName = "out_trade_no")
    @JacksonXmlCData
    var outTradeNo: String = ""

    @JacksonXmlProperty(localName = "refund_fee")
    @JacksonXmlCData
    var refundFee:Int = 0

    @JacksonXmlProperty(localName = "total_fee")
    @JacksonXmlCData
    var totalFee:Int = 0
}