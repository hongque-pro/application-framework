package com.labijie.application.payment.providers.wechat.model

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlCData
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.labijie.application.thridparty.wechat.WechatResponse

class WechatCloseResponse: WechatResponse() {

    @JacksonXmlProperty(localName = "appid")
    @JacksonXmlCData
    var appId: String = ""

    @JacksonXmlProperty(localName = "mch_id")
    @JacksonXmlCData
    var mchId: String = ""

    @JacksonXmlProperty(localName = "nonce_str")
    @JacksonXmlCData
    var nonceStr: String = ""

}