package com.labijie.application.payment

import com.labijie.application.ErrorDescription

object PaymentErrors {
    @ErrorDescription("支付渠道不支持，可能由于服务器关闭了该支付渠道。")
    const val UnsupportedPaymentProvider = "unsupported_payment_provider"

    @ErrorDescription("支付交易已存在，请勿重复支付。")
    const val DuplexPaymentTrade = "duplex_payment_trade"
}