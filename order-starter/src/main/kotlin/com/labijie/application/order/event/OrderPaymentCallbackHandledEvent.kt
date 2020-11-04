package com.labijie.application.order.event

import com.labijie.application.order.models.PaymentResult
import org.springframework.context.ApplicationEvent
import kotlin.reflect.KClass

class OrderPaymentCallbackHandledEvent(
    source:Any,
    val orderType:KClass<*>,
    val orderSnapshot: Any,
    val paymentResult: PaymentResult) : ApplicationEvent(source)