package com.labijie.application.order.models

import com.labijie.application.order.PaymentStatus
import com.labijie.application.payment.DeductMode
import org.hibernate.validator.constraints.Length
import java.math.BigDecimal
import java.time.Duration
import javax.validation.constraints.NotBlank

/**
 * 标准化订单模型
 */
open class NormalizedOrder(
    var id:Long = 0,
    var status: OrderStatus = OrderStatus.OPENED,
    var paymentStatus: PaymentStatus = PaymentStatus.CREATED,
    /**
     * 待支付金额
     */
    var amountToPay: BigDecimal = BigDecimal.ZERO,
    /**
     * 支付平台
     */
    var paymentProvider:String = "",
    var deductMode: DeductMode = DeductMode.PROACTIVE,
    var userId:Long = 0,

    @get:NotBlank
    @get:Length(max=128)
    val subject:String = "",

    /**
     * 付款交易的 id
     */
    var paymentTradeId:Long = 0,
    /**
     * 第三方支付平台付款交易 id
     */
    var platformPaymentTradeId:String = "",

    /**
     * 支付超时时间
     */
    var timePaymentExpired:Long = System.currentTimeMillis() + Duration.ofHours(2).toMillis(),

    /**
     * 订单创建时间
     */
    var timeCreated:Long = System.currentTimeMillis(),

    /**
     * 订单完成付款时间
     */
    var timePaid:Long = 0,

    /**
     * 订单完成退款时间
     */
    var timeRefund:Long = 0,

    /**
     * 订单当前状态、付款状态最后更变的时间
     */
    var timeEffected: Long = System.currentTimeMillis(),

    /**
     * 订单被关闭的时间
     */
    var timeClosed:Long = 0
)