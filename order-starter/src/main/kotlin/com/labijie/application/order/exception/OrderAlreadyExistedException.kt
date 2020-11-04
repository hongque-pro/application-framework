package com.labijie.application.order.exception

import com.labijie.application.order.OrderErrors

class OrderAlreadyExistedException(orderType: String, orderId:Long, cause:Throwable? = null)
    : OrderException("Order with id: $orderId was exsited ( order type: $orderType ) .", errorCode = OrderErrors.OrderAlreadyExisted, cause = cause)