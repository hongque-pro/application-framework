package com.labijie.application.order.exception

import com.labijie.application.order.OrderErrors

class OrderAlreadyClosedException(orderType: String, orderId:Long, cause:Throwable? = null)
    : OrderException("Order with id: $orderId has been closed ( adapter: $orderType ) .", errorCode = OrderErrors.OrderAlreadyClosed, cause = cause)