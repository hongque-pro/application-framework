package com.labijie.application.order.exception

import com.labijie.application.order.OrderErrors
import com.labijie.application.model.PaymentStatus

class OrderInvalidPaymentStatusException constructor(message:String, cause:Throwable? = null)
    : OrderException(message, errorCode = OrderErrors.InvalidPaymentStatus, cause = cause) {

    constructor(orderType:String, orderId:Long, expectStatus: PaymentStatus, actualStatus: PaymentStatus? = null, cause:Throwable? = null)
            :this("Order expect payment status '$expectStatus', but it was ${if(actualStatus != null) "'$actualStatus'" else "not"} ( order type: '$orderType', id: $orderId ) .", cause)
}