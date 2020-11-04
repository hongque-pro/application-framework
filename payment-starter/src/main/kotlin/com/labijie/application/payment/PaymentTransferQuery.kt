package com.labijie.application.payment

class PaymentTransferQuery(
    val tradeId:String,
    val platformTradeId:String,
    override var mode: TradeMode = TradeMode.Merchant,
    override var platformMerchantKey: String = ""

): IPlatformIsvParameter