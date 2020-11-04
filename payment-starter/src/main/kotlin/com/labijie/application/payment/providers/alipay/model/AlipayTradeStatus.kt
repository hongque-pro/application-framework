package com.labijie.application.payment.providers.alipay.model

object AlipayTradeStatus {
    /**
     * 交易创建，等待买家付款
     */
    const val WAIT_BUYER_PAY = "WAIT_BUYER_PAY"
    /**
     * 未付款交易超时关闭，或支付完成后全额退款
     */
    const val TRADE_CLOSED = "TRADE_CLOSED"

    /**
     *交易支付成功
     */
    const val TRADE_SUCCESS = "TRADE_SUCCESS"

    /**
     * 交易结束，不可退款
     */
    const val TRADE_FINISHED = "TRADE_FINISHED"
}