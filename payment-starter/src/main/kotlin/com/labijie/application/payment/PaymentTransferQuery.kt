package com.labijie.application.payment

class PaymentTransferQuery(
    val tradeId:String,
    /**
     * 标识 @param tradeId 是否是支付平台的 id, true 表示支付平台 id，否则为业务系统 id
     */
    val isPlatformTradeId:Boolean = false,
)