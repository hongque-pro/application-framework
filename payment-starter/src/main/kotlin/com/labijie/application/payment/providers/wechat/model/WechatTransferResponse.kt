package com.labijie.application.payment.providers.wechat.model

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlCData
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.labijie.application.thridparty.wechat.WechatResponse

class WechatTransferResponse : WechatResponse() {

    @JacksonXmlProperty(localName = "partner_trade_no")
    @JacksonXmlCData
    var partnerTradeNo: String = ""

    @JacksonXmlProperty(localName = "payment_no")
    @JacksonXmlCData
    var paymentNo: String = ""

    @JacksonXmlProperty(localName = "payment_time")
    @JacksonXmlCData
    var paymentTime: String = ""
}