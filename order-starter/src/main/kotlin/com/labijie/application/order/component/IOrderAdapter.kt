package com.labijie.application.order.component

import com.labijie.application.model.PaymentStatus
import com.labijie.application.order.models.NormalizedOrder
import com.labijie.application.order.models.PaymentEffect
import kotlin.reflect.KClass

interface IOrderAdapter<T:Any> {
    /**
     * 订单类型
     */
    val orderType: KClass<T>

    /**
     * 适配订单模型
     */
    fun adaptOrder(order:T) : NormalizedOrder

    /**
     * 适配器名称（只允许小写字母、下划线、减号和数字）
     */
    val orderTypeName:String
    get() = this.orderType.java.simpleName.lowercase()

    fun getOrderById(orderId:Long) : T?

    /**
     * 变更订单的支付逻辑
     */
    fun effectPayment(orderId:Long, value:PaymentEffect, currentStatus: PaymentStatus) : Boolean

    /**
     * 变更订单的支付逻辑
     */
    fun effectPayment(orderId:Long, value:PaymentEffect, minStatus: PaymentStatus?, statusMax: PaymentStatus?) : Boolean
}