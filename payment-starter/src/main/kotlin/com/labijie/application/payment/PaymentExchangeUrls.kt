package com.labijie.application.payment

open class PaymentExchangeUrls(
    var createTradeUrl:String = "https://api.mch.weixin.qq.com/pay/unifiedorder",
    var queryTradeUrl: String = "https://api.mch.weixin.qq.com/pay/orderquery",
    var transferUrl:String = "https://api.mch.weixin.qq.com/mmpaymkttransfers/promotion/transfers",
    var queryTransferUrl:String = "https://api.mch.weixin.qq.com/mmpaymkttransfers/gettransferinfo",
    var refundUrl:String = "https://api.mch.weixin.qq.com/secapi/pay/refund",
    var queryRefundUrl:String = "https://api.mch.weixin.qq.com/pay/refundquery",
    var closeTradeUrl:String = "https://api.mch.weixin.qq.com/pay/closeorder"
)