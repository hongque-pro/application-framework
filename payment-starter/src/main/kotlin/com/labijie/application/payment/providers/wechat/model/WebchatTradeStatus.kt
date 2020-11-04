package com.labijie.application.payment.providers.wechat.model

object WebchatTradeStatus {
    /**
     * 支付成功
     */
    const val SUCCESS = "SUCCESS"
    /**
     * 转入退款
     */
    const val REFUND = "REFUND"
    /**
     * 未支付
     */
    const val NOTPAY = "NOTPAY"
    /**
     * 已关闭
     */
    const val CLOSED = "CLOSED"
    /**
     * 已撤销（刷卡支付）
     */
    const val REVOKED = "REVOKED"
    /**
     * 用户支付中
     */
    const val USERPAYING = "USERPAYING"
    /**
     * 支付失败(其他原因，如银行返回失败)
     */
    const val PAYERROR = "PAYERROR"
}