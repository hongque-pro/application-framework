package com.labijie.application.payment.providers.wechat.model

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlCData
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.labijie.application.thridparty.wechat.WechatResponse

/**
 * 微信平台查询响应消息
 *
 * 参考：https://pay.weixin.qq.com/wiki/doc/api/wxa/wxa_api.php?chapter=9_2
 */
class WechatTradeQueryResponse : WechatResponse() {

    @JacksonXmlProperty(localName = "openid")
    @JacksonXmlCData
    var openId:String = ""

    @JacksonXmlProperty(localName = "sub_openid")
    @JacksonXmlCData
    var subOpenid:String = ""

//    @JacksonXmlProperty(localName = "trade_type")
////    @JacksonXmlCData
////    var tradeType:String = ""

    @JacksonXmlProperty(localName = "trade_state")
    @JacksonXmlCData
    var tradeState:String = ""

    @JacksonXmlProperty(localName = "transaction_id")
    @JacksonXmlCData
    var transactionId:String = ""

    @JacksonXmlProperty(localName = "time_end")
    @JacksonXmlCData
    var timeEnd:String = ""

    @JacksonXmlProperty(localName = "out_trade_no")
    @JacksonXmlCData
    var outTradeNo: String = ""

    @JacksonXmlProperty(localName = "total_fee")
    @JacksonXmlCData
    var totalFee:String = ""
}