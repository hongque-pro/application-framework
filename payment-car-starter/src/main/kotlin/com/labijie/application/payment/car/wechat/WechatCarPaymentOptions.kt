package com.labijie.application.payment.car.wechat

import com.labijie.application.payment.configuration.PaymentAutoConfiguration
import org.springframework.boot.context.properties.ConfigurationProperties

/**
 * Author: Anders Xiao
 * Date: Created in 2020/2/27 15:48
 * Copyright: Copyright (c) 2020
 * Github: https://github.com/endink
 */

@ConfigurationProperties("${PaymentAutoConfiguration.PROVIDERS_CONFIG_PREFIX}.wechat.car")
class WechatCarPaymentOptions{

    var isvCreateTradeUrl:String = "https://api.mch.weixin.qq.com/vehicle/partnerpay/payapply"
    var isvQueryTradeUrl:String = "https://api.mch.weixin.qq.com/transit/partnerpay/queryorder"
    var createTradeUrl:String = "https://api.mch.weixin.qq.com/vehicle/pay/payapply"
    var queryTradeUrl:String = "https://api.mch.weixin.qq.com/transit/queryorder"
    var refundUrl:String = "https://api.mch.weixin.qq.com/secapi/pay/refund"
    var queryRefundUrl:String = "https://api.mch.weixin.qq.com/pay/refundquery"
}