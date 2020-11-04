package com.labijie.application.order.exception

import com.labijie.application.order.OrderErrors

class OrderRefundException (orderType: String, orderId:Long, cause:Throwable? = null)
    : OrderException("Order with id: $orderId refund fault ( order type: $orderType ) .", errorCode = OrderErrors.OrderRefundFailed, cause = cause)