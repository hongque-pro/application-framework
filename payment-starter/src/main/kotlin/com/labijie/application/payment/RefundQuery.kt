package com.labijie.application.payment

data class RefundQuery(
    var tradeId: String,
    var refundId: String,
    var isPlatformTradeId:Boolean = false,
    override var mode: TradeMode = TradeMode.Merchant,
    override var platformMerchantKey: String = ""
) : IPlatformIsvParameter