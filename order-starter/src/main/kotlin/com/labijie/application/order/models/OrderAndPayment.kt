package com.labijie.application.order.models

import com.labijie.application.payment.PaymentTradeCreationResult

data class OrderAndPayment<T:Any>(val orderSnapshot:T, val payment: PaymentTradeCreationResult)