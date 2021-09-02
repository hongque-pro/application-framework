package com.labijie.application.order.event

import com.labijie.application.order.component.IOrderAdapter
import com.labijie.application.order.models.NormalizedOrder
import com.labijie.application.order.models.PaymentEffect
import org.springframework.context.ApplicationEvent

class OrderRefundCompletedEvent(
  source: Any,
  val orderAdapter: IOrderAdapter<*>,
  val orderSnapshot: NormalizedOrder,
  val paymentEffect: PaymentEffect,
) : ApplicationEvent(source)