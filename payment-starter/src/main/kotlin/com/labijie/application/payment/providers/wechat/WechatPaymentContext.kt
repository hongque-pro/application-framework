package com.labijie.application.payment.providers.wechat

import com.labijie.application.payment.PlatformTrade
import com.labijie.application.payment.TradeMode
import com.labijie.application.payment.scene.PaymentContext
import com.labijie.application.payment.configuration.PaymentProperties

class WechatPaymentContext(
    tradeMode: TradeMode,
    paymentProvider: WechatPaymentProvider,
    paymentProperties: PaymentProperties,
    options: WechatPaymentOptions
) : PaymentContext<WechatPaymentProvider, WechatPaymentOptions>(tradeMode, paymentProvider, paymentProperties, options) {
}