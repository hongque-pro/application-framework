package com.labijie.application.payment

import java.math.BigDecimal

data class PaymentTradeQueryResult(
    val tradeId: String = "",
    val platformTradeId: String = "",
    var platformBuyerId:String = "",
    val amount: BigDecimal = BigDecimal.ZERO,
    var status: PaymentTradeStatus,
    var platformStatus: String,
    val paidTime:Long = 0)