package com.labijie.application.order

import com.labijie.application.IDescribeEnum

enum class PaymentStatus(override val code: Byte, override val description: String) : IDescribeEnum<Byte> {
    /***
     *
     * 该状态用于定义订单预创建阶段，如果创建后即可支付应该跳过该状态
     */
    CREATED(10, "已创建"),
    UNPAID(20, "待付款"),
    PAYING(30, "付款中"),
    PAID(40, "已付款"),
    REFUNDING(50, "退款中"),
    REFUNDED(60, "退款")
}