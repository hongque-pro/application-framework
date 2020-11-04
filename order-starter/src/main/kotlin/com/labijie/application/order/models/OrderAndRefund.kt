package com.labijie.application.order.models

import com.labijie.application.payment.RefundResult

data class OrderAndRefund<T:Any>(val orderSnapshot:T, val refund: RefundResult)