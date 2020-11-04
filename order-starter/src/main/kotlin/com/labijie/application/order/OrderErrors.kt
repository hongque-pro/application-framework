package com.labijie.application.order

import com.labijie.application.ErrorDescription
import com.labijie.application.order.exception.OrderAlreadyExistedException

object OrderErrors {
    @ErrorDescription("订单已存在")
    const val OrderAlreadyExisted = "order_already_existed"

    @ErrorDescription("订单不存在")
    const val OrderNotFound = "order_not_found"

    @ErrorDescription("订单已关闭")
    const val OrderAlreadyClosed = "order_already_closed"

    @ErrorDescription("订单支付状态不正确")
    const val InvalidPaymentStatus = "order_invalid_payment_status"

    @ErrorDescription("付款交易不存在")
    const val PaymentTradeNotFound = "pay_trade_not_found"

    @ErrorDescription("退款失败")
    const val OrderRefundFailed = "order_refund_failed"
}