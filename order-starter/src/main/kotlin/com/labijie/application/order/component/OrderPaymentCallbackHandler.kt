package com.labijie.application.order.component

import com.labijie.application.order.IOrderWorkflow
import com.labijie.application.order.event.OrderPaymentCallbackHandledEvent
import com.labijie.application.order.exception.OrderException
import com.labijie.application.order.models.PaymentResult
import com.labijie.application.payment.callback.IPaymentCallbackHandler
import com.labijie.application.payment.callback.PaymentCallbackContext
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.ApplicationEventPublisherAware

class OrderPaymentCallbackHandler(
    private val orderWorkflow: IOrderWorkflow) : IPaymentCallbackHandler, ApplicationEventPublisherAware {
    private lateinit var eventPublisher : ApplicationEventPublisher

    override fun setApplicationEventPublisher(applicationEventPublisher: ApplicationEventPublisher) {
        eventPublisher = applicationEventPublisher
    }

    override fun handleCallback(context: PaymentCallbackContext) {

        val tradeId = context.request.tradeId.toLong()
        val orderTypeName = context.state ?: throw OrderException("Payment callback state (order type name) missed.")

        val orderType = orderWorkflow.getOrderType(orderTypeName)

        val paymentResult = PaymentResult(
            tradeId,
            context.request.platformBuyerId,
            context.request.amount,
            context.request.status,
            context.request.timePaid,
            context.request.platformTradeId
        )
        //完成回调
        val order = orderWorkflow.endPayment(paymentResult,orderType =  orderType)

        if(this::eventPublisher.isInitialized){
            val event = OrderPaymentCallbackHandledEvent(this, orderType, order, paymentResult)
            this.eventPublisher.publishEvent(event)
        }
    }
}