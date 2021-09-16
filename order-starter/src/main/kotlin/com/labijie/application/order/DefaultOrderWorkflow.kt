package com.labijie.application.order

import com.labijie.application.configure
import com.labijie.application.exception.DataMaybeChangedException
import com.labijie.application.model.OrderStatus
import com.labijie.application.model.PaymentStatus
import com.labijie.application.order.component.IOrderAdapter
import com.labijie.application.order.component.IOrderAdapterLocator
import com.labijie.application.order.data.OrderPaymentTrade
import com.labijie.application.order.data.OrderPaymentTradeExample
import com.labijie.application.order.data.mapper.OrderPaymentTradeMapper
import com.labijie.application.order.event.*
import com.labijie.application.order.exception.*
import com.labijie.application.order.models.*
import com.labijie.application.payment.*
import com.labijie.application.toEnum
import com.labijie.infra.IIdGenerator
import com.labijie.infra.json.JacksonHelper
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.ApplicationEventPublisherAware
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.support.TransactionSynchronizationManager
import org.springframework.transaction.support.TransactionTemplate
import java.math.BigDecimal
import java.time.Duration
import kotlin.concurrent.thread
import kotlin.reflect.KClass

/**
 * 表示一个订单工作流，简化业务处理订单并发问题的复杂度
 */
open class DefaultOrderWorkflow(
    private val orderPaymentTradeMapper: OrderPaymentTradeMapper,
    private val transactionTemplate: TransactionTemplate,
    private val paymentUtils: PaymentUtils,
    private val idGenerator: IIdGenerator,
    private val adapterLocator: IOrderAdapterLocator
) : IOrderWorkflow, ApplicationEventPublisherAware {

    companion object {
        @JvmStatic
        private val log = LoggerFactory.getLogger(DefaultOrderWorkflow::class.java)
    }

    private lateinit var applicationEventPublisher: ApplicationEventPublisher

    override fun getOrderType(orderTypeName: String): KClass<*> {
        return adapterLocator.findAdapter(orderTypeName).orderType
    }

    override fun <T : Any> beginPayment(
        parameter: OrderPaymentInput,
        orderType: KClass<T>,
        paymentScene: Any?,
        allowPayWithCreatedStatus: Boolean
    ): OrderAndPayment<T> {
        val adapter = adapterLocator.findAdapter(orderType)
        if (parameter.orderId <= 0) {
            throw OrderNotFoundException(adapter.orderTypeName, parameter.orderId)
        }
        val data = adapter.getOrderById(parameter.orderId) ?: throw OrderNotFoundException(
            adapter.orderTypeName,
            parameter.orderId
        )
        val order = adapter.adaptOrder(data)
        if (order.status == OrderStatus.CLOSED) {
            throw OrderAlreadyClosedException(adapter.orderTypeName, order.id)
        }

        //简单检查，杜绝大多数并发问题
        if (order.paymentStatus.code >= PaymentStatus.PAYING.code) {
            throw OrderInvalidPaymentStatusException(
                adapter.orderTypeName,
                order.id,
                PaymentStatus.CREATED,
                order.paymentStatus
            )
        }

        val tradeId = parameter.paymentTradeId ?: idGenerator.newId()
        //准备第三方平台调用参数
        val trade = createPlatformTrade(tradeId, parameter, order, adapter.orderTypeName).apply {
            this.state = adapter.orderTypeName
            this.scene = paymentScene
        }
        //一定要先保证交易单存在，否则回调时候无法找到订单
        createOrderPayment(
            tradeId,
            adapter.orderTypeName,
            parameter,
            trade.amount
        )
        //调用第三方创建支付
        this.onPlatformTradeCreated(trade)
        val result = paymentUtils.createTrade(parameter.paymentProvider, trade)

        //更新订单状态，重复支付可能导致并发更新失败，这里只需要抛出异常阻止用户支付，放到后面处理
        transactionTemplate.execute {
            //需要更新到订单的属性：支付状态，交易号，第三方单号，支付渠道，支付方式，支付超时时间
            val value = PaymentEffect(
                status = PaymentStatus.PAYING,
                tradeId = tradeId,
                paymentProvider = parameter.paymentProvider,
                deductMode = result.deductMode,
                timeEffected = System.currentTimeMillis(),
                paidExpired = System.currentTimeMillis() + Duration.ofMinutes(parameter.timeoutMinutes.toLong())
                    .toMillis(),
                platformTradeId = result.platformTradeId
            )

            try {
                val updated = if (allowPayWithCreatedStatus) adapter.effectPayment(
                    order.id,
                    value,
                    null,
                    PaymentStatus.UNPAID
                ) else adapter.effectPayment(order.id, value, PaymentStatus.UNPAID)
                if (!updated) {
                    //虽然更新订单失败，但是付款单已经创建了
                    throw OrderInvalidPaymentStatusException(adapter.orderTypeName, order.id, PaymentStatus.UNPAID)
                }
            } finally {
                //无论订单是否更新成功，都尝试更新一次付款交易单, 但是可以异步操作，不影响当前事务环境
                this.updateOrderPaymentAsync(
                    tradeId,
                    System.currentTimeMillis(),
                    platformId = result.platformTradeId,
                    tag = result.tag
                )
            }
        }
        return OrderAndPayment(data, result)
    }


    override fun <T : Any> endPayment(paymentResult: PaymentResult, orderType: KClass<T>): T {
        if (paymentResult.paymentTradeId <= 0) {
            throw PaymentTradeNotFoundException(paymentResult.paymentTradeId)
        }

        val trade = orderPaymentTradeMapper.selectByPrimaryKey(paymentResult.paymentTradeId)
            ?: throw PaymentTradeNotFoundException(paymentResult.paymentTradeId)

        //验证购买人和金额受否匹配
        if (trade.amount.compareTo(paymentResult.amount) != 0 || (!paymentResult.platformBuyerId.isNullOrBlank() && !trade.platformBuyerId.isNullOrBlank() && trade.platformBuyerId != paymentResult.platformBuyerId)) {
            throw PaymentTradeNotFoundException(
                """
                Trade was existed, but amount or buyer id was not same. 
                amount: ${trade.amount.toPlainString()}, and result: ${paymentResult.amount.toPlainString()}
                buyer: ${trade.platformBuyerId}, and result: ${paymentResult.platformBuyerId}
            """.trimIndent()
            )
        }
        //开始处理订单逻辑
        return endPayment(null, trade, paymentResult, orderType)
    }

    override fun <T : Any> endPaymentForZeroAmount(
        orderId: Long,
        orderType: KClass<T>,
        status: PaymentTradeStatus,
        timeEffected: Long
    ): T {
        val result = PaymentResult(
            0,
            null,
            BigDecimal.ZERO,
            PaymentTradeStatus.Paid, timeEffected, ""
        )
        return endPayment(orderId, null, result, orderType)
    }


    private fun <T : Any> endPayment(
        orderId: Long?,
        trade: OrderPaymentTrade?,
        result: PaymentResult,
        orderType: KClass<T>
    ): T {

        if (result.status == PaymentTradeStatus.WaitPay) {
            throw IllegalArgumentException("End payment with status '${result.status.toString()}' was unsupported.")
        }

        return try {
            val adapter = adapterLocator.findAdapter(orderType)

            var data: T? = null
            //付款成功的情况
            if (result.status == PaymentTradeStatus.Paid) {
                data = processOrderPaid(adapter, orderId, trade, result.platformTradeId, result.timeEffected)
            }

            //处理交易超时的情况
            if (result.status == PaymentTradeStatus.Close) {
                data = processOrderPayTimeout(adapter, orderId, trade, result.timeEffected)
            }
            data!!
        } catch (e: Exception) {
            throw e
        } finally {
            if (result.platformTradeId.isNotBlank()) {
                //无论如何，尝试一次异步更新付款单
                this.updateOrderPaymentAsync(
                    result.paymentTradeId,
                    result.timeEffected,
                    result.status,
                    result.platformTradeId
                )
            }
        }
    }

    private fun <T : Any> processOrderPayTimeout(
        adapter: IOrderAdapter<T>,
        orderId: Long?,
        trade: OrderPaymentTrade?,
        timeEffected: Long
    ): T {
        val (isPaid, entry) = adapter.isPaid(orderId, trade)
        val (order, data) = entry
        if (isPaid) {
            //说明状态已支付，可能由于多次付款，但是其中一次已经付款成功，无需更新订单状态，认为处理成功
            return data
        }
        if (order.paymentTradeId == (trade?.id ?: 0L)) {
            //付款单号变化忽略处理，允许重新发起付款
            val statusValues = PaymentEffect(
                status = PaymentStatus.UNPAID,
                tradeId = 0,
                paymentProvider = "",
                deductMode = DeductMode.PROACTIVE,
                timeEffected = timeEffected,
                platformTradeId = ""
            )
            //虽然这里交易单号有并发付款单号的危险，但是支付成功回调会进行覆盖补偿，也无需再考虑并发
            this.transactionTemplate.execute {
                val effected = adapter.effectPayment(order.id, statusValues, PaymentStatus.PAYING)
                if (effected && this::applicationEventPublisher.isInitialized) {
                    val event = OrderPaymentCompletingEvent(this, adapter, entry.first, statusValues)
                    this.applicationEventPublisher.publishEvent(event)

                    val completedEvent = OrderPaymentCompletedEvent(this, adapter, entry.first, statusValues)
                    TransactionSynchronizationManager.registerSynchronization(
                        OrderPaymentTransactionHook(
                            this.applicationEventPublisher,
                            completedEvent
                        )
                    )
                }
                effected
            }
        }
        return data
    }


    private fun <T : Any> processOrderPaid(
        adapter: IOrderAdapter<T>,
        orderId: Long?,
        trade: OrderPaymentTrade?,
        platformTradeId: String,
        timeEffected: Long
    ): T {
        val (isIdempotent, entry) = adapter.isPaid(orderId, trade)
        var data = entry.second
        //先查询检查幂等
        if (!isIdempotent) {
            val statusValues = PaymentEffect(
                status = PaymentStatus.PAID,
                tradeId = trade?.id ?: 0,
                platformTradeId = platformTradeId,
                paymentProvider = trade?.paymentProvider ?: "",
                deductMode = trade?.paymentType?.toEnum() ?: DeductMode.PROACTIVE,
                timeEffected = timeEffected
            )

            val updated = this.transactionTemplate.execute {
                val effected = adapter.effectPayment(entry.first.id, statusValues, null, PaymentStatus.PAYING)
                if (effected && this::applicationEventPublisher.isInitialized) {
                    val event = OrderPaymentCompletingEvent(this, adapter, entry.first, statusValues)
                    this.applicationEventPublisher.publishEvent(event)

                    val completedEvent = OrderPaymentCompletedEvent(this, adapter, entry.first, statusValues)
                    TransactionSynchronizationManager.registerSynchronization(
                        OrderPaymentTransactionHook(
                            this.applicationEventPublisher,
                            completedEvent
                        )
                    )
                }
                effected
            }

            if (!(updated!!)) {
                //乐观并发了，检查是否幂等
                val (isIdempotent2, entry2) = adapter.isPaid(orderId, trade)
                data = entry2.second
                if (!isIdempotent2) {
                    log.error(
                        "Unable to update order payment status, update fault but it was not idempotent, please check order status" +
                                " ( order id: ${entry2.first.id}, type: ${adapter.orderTypeName}  )."
                    )
                }
            }
        }
        return data
    }

    private fun <T : Any> IOrderAdapter<T>.isPaid(
        orderId: Long?,
        trade: OrderPaymentTrade?
    ): Pair<Boolean, Pair<NormalizedOrder, T>> {
        if (orderId == null && trade == null) {
            throw OrderException("order id and order payment trade can not be both <null>")
        }

        val id = (orderId ?: trade?.orderId) ?: 0
        val data = this.getOrderById(id) ?: throw OrderNotFoundException(
            this.orderTypeName,
            id
        ) //订单不存在抛出异常，正常情况不应该出现该错误

        val order = this.adaptOrder(data)

        if (trade == null) {  //交易单不存在，说明金额为 0 无需付款
            return Pair(false, Pair(order, data))
        }
        if (order.paymentStatus.code >= PaymentStatus.PAID.code) {
            //幂等了，单号交易号如果不对说明重复付款，记录警告
            if (order.paymentTradeId != trade.id) {
                log.warn("Orders have multiple payment trade, the users may have duplicate payment behaviors. ( order id: ${order.id}, type: ${trade.orderType} )")
            }
            return Pair(true, Pair(order, data))
        }
        return Pair(false, Pair(order, data))
    }

    protected open fun onPlatformTradeCreated(trade: PlatformTrade) {
    }

    private fun updateOrderPaymentAsync(
        tradeId: Long,
        timeEffected: Long,
        status: PaymentTradeStatus? = null,
        platformId: String? = null,
        tag: Map<String, String>? = null
    ) {
        thread {
            try {
                this.updateOrderPayment(tradeId, timeEffected, status, platformId, tag)
            } catch (e: Exception) {
                log.error("Async update payment trade fault.", e)
            }
        }
    }

    private fun updateOrderPayment(
        tradeId: Long,
        timeEffected: Long,
        status: PaymentTradeStatus? = null,
        platformId: String? = null,
        tag: Map<String, String>? = null
    ) {
        this.transactionTemplate.execute {
            val trade = OrderPaymentTrade().apply {
                this.id = tradeId
                this.status = status?.code
                this.platformId = platformId
                this.tag = if (tag != null) JacksonHelper.serializeAsString(tag) else null
                this.timeEffected = timeEffected
            }
            orderPaymentTradeMapper.updateByPrimaryKeySelective(trade)
        }
    }

    private fun createOrderPayment(
        tradeId: Long,
        type: String,
        paymentInput: OrderPaymentInput,
        payAmount: BigDecimal
    ): OrderPaymentTrade {
        val now = System.currentTimeMillis()

        return this.transactionTemplate.execute {
            val orderPaymentTrade = OrderPaymentTrade().apply {
                this.id = tradeId
                this.orderId = paymentInput.orderId
                this.userId = paymentInput.userId
                this.status = PaymentTradeStatus.WaitPay.code
                this.amount = payAmount
                this.method = paymentInput.method.ordinal.toByte()
                this.mode = paymentInput.mode.ordinal.toByte()
                this.orderType = type
                this.platformBuyerId = paymentInput.platformBuyerId
                this.platformId = ""
                this.paymentProvider = paymentInput.paymentProvider
                this.paymentType = paymentInput.deductMode.ordinal.toByte()
                this.platformMerchantKey = paymentInput.platformMerchantKey
                this.timeCreated = now
                this.timeEffected = now
                this.timeExpired = now + paymentInput.timeoutMinutes * 60 * 1000
                this.tag = "{}"
            }

            orderPaymentTradeMapper.insert(orderPaymentTrade)
            orderPaymentTrade
        }!!
    }

    /**
     * 创建第三方平台交易参数
     */
    private fun createPlatformTrade(
        tradeId: Long,
        parameter: OrderPaymentInput,
        order: NormalizedOrder,
        orderTypeName: String
    ): PlatformTrade {
        return PlatformTrade().apply {
            this.tradeId = tradeId.toString()
            this.mode = parameter.mode
            this.timeoutMinutes = parameter.timeoutMinutes
            this.platformBuyerId = parameter.platformBuyerId
            this.subject = order.subject
            this.amount = order.amountToPay
            this.method = parameter.method
            this.state = orderTypeName
            this.platformMerchantKey = parameter.platformMerchantKey
        }
    }

    override fun setApplicationEventPublisher(applicationEventPublisher: ApplicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher
    }

    override fun <T : Any> beginRefund(refund: OrderRefundInput, orderType: KClass<T>): OrderAndRefund<T> {
        val adapter = adapterLocator.findAdapter(orderType)
        if (refund.orderId <= 0) {
            throw OrderNotFoundException(adapter.orderTypeName, refund.orderId)
        }
        val data = adapter.getOrderById(refund.orderId) ?: throw OrderNotFoundException(
            adapter.orderTypeName,
            refund.orderId
        )
        val order = adapter.adaptOrder(data)
        if (order.status == OrderStatus.CLOSED) {
            throw OrderAlreadyClosedException(adapter.orderTypeName, order.id)
        }

        //简单检查，杜绝大多数并发问题
        if (order.paymentStatus != PaymentStatus.PAID) {
            throw OrderInvalidPaymentStatusException(
                adapter.orderTypeName,
                order.id,
                PaymentStatus.PAID,
                order.paymentStatus
            )
        }

        val refundTrade = RefundTrade(
            refundId = order.paymentTradeId.toString(),
            paymentTradeId = order.platformPaymentTradeId,
            amount = order.amountToPay,
            remark = refund.remark,
            isPaymentTradeId = true,
            platformMerchantKey = refund.platformMerchantKey.orEmpty(),
            mode = refund.mode
        ).apply {
            this.state = adapter.orderTypeName
        }

        val result = paymentUtils.refund(order.paymentProvider, refundTrade)

        when (result.status) {
            RefundStatus.Doing, RefundStatus.Succeed -> {

                val status =
                    if (result.status == RefundStatus.Doing) PaymentStatus.REFUNDING else PaymentStatus.REFUNDED

                val time =
                    if (result.status == RefundStatus.Doing) System.currentTimeMillis() else result.refundTime

                val value = PaymentEffect(
                    status = status,
                    tradeId = order.paymentTradeId,
                    paymentProvider = order.paymentProvider,
                    deductMode = order.deductMode,
                    timeEffected = time
                )

                transactionTemplate.configure(isolationLevel = Isolation.READ_COMMITTED).execute {

                    val updated = adapter.effectPayment(order.id, value, PaymentStatus.PAID)
                    if (!updated) {
                        val newOrder = adapter.getNormalizedOrder(order.id) //幂等不再抛出异常
                        if(newOrder.paymentStatus != PaymentStatus.REFUNDED && newOrder.paymentStatus != PaymentStatus.REFUNDING) {
                            throw OrderInvalidPaymentStatusException(
                                adapter.orderTypeName,
                                order.id,
                                PaymentStatus.PAID,
                                newOrder.paymentStatus
                            )
                        }
                    }
                }
            }
            RefundStatus.Fail -> {
                throw OrderRefundException(adapter.orderTypeName, order.id)
            }
        }
        return OrderAndRefund(data, result)
    }

    override fun <T : Any> endRefund(orderId: Long, orderType: KClass<T>, succeed: Boolean, timeEffected: Long?) {
        val adapter = adapterLocator.findAdapter(orderType)
        if (orderId <= 0) {
            throw OrderNotFoundException(adapter.orderTypeName, orderId)
        }
        val data = adapter.getOrderById(orderId) ?: throw OrderNotFoundException(
            adapter.orderTypeName,
            orderId
        )
        val order = adapter.adaptOrder(data)
        if (order.status == OrderStatus.CLOSED) {
            throw OrderAlreadyClosedException(adapter.orderTypeName, order.id)
        }

        val value = PaymentEffect(
            status = if(succeed) PaymentStatus.REFUNDED else PaymentStatus.PAID,
            tradeId = order.paymentTradeId,
            paymentProvider = order.paymentProvider,
            deductMode = order.deductMode,
            timeEffected = timeEffected ?: System.currentTimeMillis()
        )

        transactionTemplate.configure(isolationLevel = Isolation.READ_COMMITTED).execute {
            val updated = adapter.effectPayment(order.id, value, PaymentStatus.REFUNDING)
            if (!updated) {
                val newOrder = adapter.getNormalizedOrder(orderId) //幂等不再抛出异常
                if ((succeed && newOrder.paymentStatus != PaymentStatus.REFUNDED) || (!succeed && newOrder.paymentStatus != PaymentStatus.PAID)) {
                    throw OrderInvalidPaymentStatusException(adapter.orderTypeName, order.id, PaymentStatus.REFUNDING, newOrder.paymentStatus)
                }
            }

            this.applicationEventPublisher.publishEvent(
                OrderRefundCompletingEvent(this, adapter, order, value)
            )

            TransactionSynchronizationManager.registerSynchronization(
                OrderRefundTransactionHook(
                    this.applicationEventPublisher,
                    OrderRefundCompletedEvent(this, adapter, order, value),
                )
            )
        }
    }

    private fun <T:Any> IOrderAdapter<T>.getNormalizedOrder(orderId: Long): NormalizedOrder {
        val newData = this.getOrderById(orderId) ?: throw OrderNotFoundException(
            this.orderTypeName,
            orderId
        )
        val newOrder = this.adaptOrder(newData) //幂等不再抛出异常
        return newOrder
    }

    override fun <T : Any> closeOrder(orderCloseInput: OrderCloseInput<T>): TradeCloseStatus {
        orderCloseInput.type ?: throw OrderAdapterNotFoundException("无此类订单")
        val adapter = adapterLocator.findAdapter(orderCloseInput.type!!)

        val paymentOrders = orderPaymentTradeMapper.selectByExample(OrderPaymentTradeExample().apply {
            this.createCriteria()
                .andOrderTypeEqualTo(adapter.orderTypeName).andOrderIdEqualTo(orderCloseInput.orderId)
        })

        if(paymentOrders.isNullOrEmpty()) {
            throw OrderNotFoundException(adapter.orderTypeName, orderCloseInput.orderId)
        }

        val paymentOrder = paymentOrders[0]
        if(paymentOrder.status == PaymentTradeStatus.Paid.code) {
            throw OrderInvalidPaymentStatusException("订单不为待支付状态，无法关闭")
        } else if(paymentOrder.status == PaymentTradeStatus.Close.code) {
            return TradeCloseStatus.ORDER_CLOSED
        }

        val res = paymentUtils.closeTrade(paymentOrder.paymentProvider, TradeCloseParam(outTradeNo = paymentOrder.id?.toString()))

        if(res.status == TradeCloseStatus.SUCCESS || res.status == TradeCloseStatus.ORDER_CLOSED) {
            OrderPaymentTrade().apply {
                this.id = paymentOrder.id
                this.timeEffected = System.currentTimeMillis()
                this.status = PaymentTradeStatus.Close.code

                val cnt = orderPaymentTradeMapper.updateByPrimaryKeySelective(this)
                if(cnt != 1) {
                    throw DataMaybeChangedException("订单可能已被其他程序修改")
                }
            }
        }

        return res.status
    }
}