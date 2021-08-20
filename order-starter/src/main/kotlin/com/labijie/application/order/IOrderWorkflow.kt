package com.labijie.application.order

import com.labijie.application.order.models.*
import com.labijie.application.payment.PaymentTradeStatus
import com.labijie.application.payment.RefundResult
import com.labijie.application.payment.TradeCloseStatus
import kotlin.reflect.KClass

interface IOrderWorkflow {
    fun getOrderType(orderTypeName: String): KClass<*>

    /**
     * 发起支付流程
     *
     * @param parameter 支付参数
     * @param orderType 订单类型
     * @param paymentScene 支付场景
     * @param allowPayWithCreatedStatus 是否允许 Create 状态下的订单支付，如果为 true, 只有  UNPAID 状态的订单可以发起支付，否则只要小于等于 UNPAID 都可以支付。
     */
    fun <T : Any> beginPayment(
        parameter: OrderPaymentInput,
        orderType: KClass<T>,
        paymentScene:Any? = null,
        allowPayWithCreatedStatus: Boolean = false
    ): OrderAndPayment<T>

    /**
     * @param paymentResult 和支付结果相关的参数
     */
    fun <T : Any> endPayment(paymentResult: PaymentResult, orderType: KClass<T>): T

    fun <T: Any> endPaymentForZeroAmount(
        orderId:Long,
        orderType: KClass<T>,
        status:PaymentTradeStatus = PaymentTradeStatus.Paid,
        timeEffected: Long = System.currentTimeMillis()) : T

    fun <T: Any> beginRefund(refund: OrderRefundInput, orderType: KClass<T>) : OrderAndRefund<T>

    fun <T:Any> endRefund(orderId:Long, orderType: KClass<T>, succeed:Boolean, timeEffected: Long? = null)

    fun <T:Any> closeOrder(orderCloseInput: OrderCloseInput<T>): TradeCloseStatus
}