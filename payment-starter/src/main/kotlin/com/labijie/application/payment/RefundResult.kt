package com.labijie.application.payment

import java.math.BigDecimal

data class RefundResult(
    val paymentProvider: String,
    val refundId: String,
    var platformRefundId: String,
    var orderAmount: BigDecimal,
    var refundAmount: BigDecimal,
    var paymentTradeId: String,
    var platformPaymentTradeId: String,
    var refundTime: Long,
    var status: RefundStatus
)