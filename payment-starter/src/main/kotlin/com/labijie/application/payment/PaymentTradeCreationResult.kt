package com.labijie.application.payment

data class PaymentTradeCreationResult(
    val tradeId:String,
    val paymentProvider:String,

    /**
     * 第三方平台交易号。
     * 支付宝为：订单号
     * 微信为：不返回平台单号
     */
    val platformTradeId:String?,

    /**
     * 支付方式
     */
    val deductMode:DeductMode = DeductMode.PROACTIVE,

    /**
     * 是否有支付成功回调
     */
    val hasCallback:Boolean = true,
    /**
     * 附加数据。
     */
    val tag:Map<String, String> = emptyMap()
)