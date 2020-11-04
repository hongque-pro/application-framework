package com.labijie.application.payment.scene

import com.labijie.application.payment.IPaymentProvider
import com.labijie.application.payment.PaymentOptions
import com.labijie.application.payment.TradeMode
import com.labijie.application.payment.configuration.PaymentProperties

open class PaymentContext<TPaymentProvider: IPaymentProvider, TOptions: PaymentOptions>(
    val tradeMode:TradeMode,
    val paymentProvider:TPaymentProvider,
    val paymentProperties: PaymentProperties,
    val options: TOptions
) {

}