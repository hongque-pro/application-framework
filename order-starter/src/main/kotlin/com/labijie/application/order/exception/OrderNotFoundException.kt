package com.labijie.application.order.exception

import com.labijie.application.order.OrderErrors

class OrderNotFoundException (message:String, cause:Throwable? = null)
    : OrderException(message, errorCode = OrderErrors.OrderNotFound, cause = cause){

    constructor(orderType: String, orderId:Long, cause:Throwable? = null) :this("Order with id: $orderId was not found ( order type: $orderType ) .", cause)
}