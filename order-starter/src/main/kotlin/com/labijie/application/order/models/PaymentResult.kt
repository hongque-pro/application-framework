package com.labijie.application.order.models

import com.labijie.application.payment.PaymentTradeStatus
import java.math.BigDecimal

data class PaymentResult(
    val paymentTradeId:Long,
    val platformBuyerId:String?,
    val amount:BigDecimal,
    val status: PaymentTradeStatus,
    val timeEffected: Long,
    val platformTradeId: String
)