package com.labijie.application.order.models

import com.labijie.application.model.PaymentStatus
import com.labijie.application.payment.DeductMode

data class PaymentEffect(
    /**
     * 订单支付状态
     */
    val status: PaymentStatus,
    /**
     * 支付交易 id.
     */
    val tradeId:Long,
    /**
     * 支付平台
     */
    val paymentProvider:String,

    /**
     * 支付类型
     */
    val deductMode: DeductMode,
    /**
     * 状态变更的时间
     */
    val timeEffected:Long = System.currentTimeMillis(),

    val paidExpired:Long? = null,


    /**
     * 支付平台的交易单号
     */
    val platformTradeId:String? = null
)