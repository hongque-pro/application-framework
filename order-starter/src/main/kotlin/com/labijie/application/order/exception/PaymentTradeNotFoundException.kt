package com.labijie.application.order.exception

import com.labijie.application.order.OrderErrors

class PaymentTradeNotFoundException constructor(message:String, cause:Throwable? = null)
    : OrderException(message, errorCode = OrderErrors.PaymentTradeNotFound, cause = cause){

    constructor(tradeId:Long, cause:Throwable? = null) : this("Payment trade with id: $tradeId was not found .", cause)
}