package com.labijie.application.payment

import java.math.BigDecimal

/**
 * 第三方平台的回调处理结果
 */
data class PaymentCallbackRequest(
    val appid:String,
    val tradeId:String,
    val platformTradeId:String,
    val status: PaymentTradeStatus,
    val timePaid: Long,
    val amount:BigDecimal,
    val platformBuyerId:String? = null,
    val originalPayload:Map<String, String> = mapOf())