package com.labijie.application.payment.providers.wechat.model

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlCData
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.labijie.application.thridparty.wechat.WechatResponse

class WechatTradeCreationResponse : WechatResponse(){
    @JacksonXmlProperty(localName = "trade_type")
    @JacksonXmlCData
    var tradeType:String = ""

    @JacksonXmlProperty(localName = "prepay_id")
    @JacksonXmlCData
    var prepayId:String = ""
}