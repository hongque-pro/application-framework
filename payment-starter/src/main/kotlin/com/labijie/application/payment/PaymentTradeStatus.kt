package com.labijie.application.payment

import com.labijie.application.IDescribeEnum

enum class PaymentTradeStatus(override val code: Byte, override val description: String) : IDescribeEnum<Byte> {
    /**
     * 交易已创建，等待用户付款
     */
    WaitPay(10, "Wait user pay"),
    /**
     * 已经付款（可以退款）
     */
    Paid(20, "User has paid"),
    /**
     * 没有付款或者已退款，并且已关闭（不可再次付款）
     */
    Close(100, "Close or cancelled")
}