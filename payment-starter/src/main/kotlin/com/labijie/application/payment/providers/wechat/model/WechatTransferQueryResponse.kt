package com.labijie.application.payment.providers.wechat.model

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlCData
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.labijie.application.thridparty.wechat.WechatResponse

class WechatTransferQueryResponse : WechatResponse() {
    @JacksonXmlProperty(localName = "partner_trade_no")
    @JacksonXmlCData
    var partnerTradeNo: String = ""

    @JacksonXmlProperty(localName = "detail_id")
    @JacksonXmlCData
    var detailId: String = ""

    @JacksonXmlProperty(localName = "status")
    @JacksonXmlCData
    var status: String = ""

    @JacksonXmlProperty(localName = "reason")
    @JacksonXmlCData
    var reason: String? = ""

    @JacksonXmlProperty(localName = "openid")
    @JacksonXmlCData
    var openId: String = ""

    @JacksonXmlProperty(localName = "amount")
    @JacksonXmlCData
    var amount: Int = 0


    @JacksonXmlProperty(localName = "transfer_time")
    @JacksonXmlCData
    var transferTime: String = ""
}