package com.labijie.application.payment

enum class TransferStatus {
    /**
     * 退款成功
     */
    Succeed,

    /**
     * 退款中
     */
    Doing,

    /**
     * 银行已退款
     */
    Refund,

    /**
     * 退款失败
     */
    Fail,
}