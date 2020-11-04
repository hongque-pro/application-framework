package com.labijie.application.order.component

import com.labijie.application.order.IOrderWorkflow
import com.labijie.application.order.data.OrderPaymentTrade
import com.labijie.application.order.data.OrderPaymentTradeExample
import com.labijie.application.order.data.mapper.OrderPaymentTradeMapper
import com.labijie.application.order.exception.OrderException
import com.labijie.application.order.exception.OrderNotFoundException
import com.labijie.application.order.exception.PaymentTradeNotFoundException
import com.labijie.application.payment.RefundStatus
import com.labijie.application.payment.callback.IRefundCallbackHandler
import com.labijie.application.payment.callback.RefundCallbackContext

class OrderRefundCallbackHandler(
    private val orderPaymentTradeMapper: OrderPaymentTradeMapper,
    private val orderWorkflow: IOrderWorkflow) : IRefundCallbackHandler {
    override fun handleCallback(context: RefundCallbackContext) {

        val tradeId = context.result.paymentTradeId.toLong()
        val trade = orderPaymentTradeMapper.selectByPrimaryKeySelective(tradeId, OrderPaymentTrade.Column.orderId) ?:
            throw PaymentTradeNotFoundException(tradeId)

        val orderTypeName = context.state ?: throw OrderException("Refund callback state (order type name) missed.")
        val orderType = orderWorkflow.getOrderType(orderTypeName)

        val succeed = when(context.result.status){
            RefundStatus.Fail -> false
            RefundStatus.Succeed->true
            else-> throw OrderException("Refund callback get an unknown state : ${RefundStatus.Doing}.")
        }

        orderWorkflow.endRefund(trade.orderId, orderType, succeed)
    }
}