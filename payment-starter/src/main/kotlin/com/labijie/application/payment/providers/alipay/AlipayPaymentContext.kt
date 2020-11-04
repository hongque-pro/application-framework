package com.labijie.application.payment.providers.alipay

import com.labijie.application.payment.TradeMode
import com.labijie.application.payment.configuration.PaymentProperties
import com.labijie.application.payment.scene.PaymentContext

class AlipayPaymentContext(
    tradeMode: TradeMode,
    paymentProvider: AlipayPaymentProvider,
    paymentProperties: PaymentProperties,
    options: AlipayPaymentOptions
) : PaymentContext<AlipayPaymentProvider, AlipayPaymentOptions>(tradeMode, paymentProvider, paymentProperties, options) {
}