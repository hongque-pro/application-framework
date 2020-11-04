package com.labijie.application.order.configuration

import com.labijie.application.order.DefaultOrderWorkflow
import com.labijie.application.order.IOrderWorkflow
import com.labijie.application.order.OrderErrorsRegistration
import com.labijie.application.order.component.*
import com.labijie.application.order.data.mapper.OrderPaymentTradeMapper
import com.labijie.application.payment.PaymentUtils
import com.labijie.application.payment.configuration.PaymentAutoConfiguration
import com.labijie.infra.IIdGenerator
import org.mybatis.spring.annotation.MapperScan
import org.springframework.beans.factory.ObjectProvider
import org.springframework.boot.autoconfigure.AutoConfigureAfter
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.transaction.support.TransactionTemplate

@Configuration
@Suppress("SpringJavaInjectionPointsAutowiringInspection")
@MapperScan(basePackageClasses = [OrderPaymentTradeMapper::class])
@AutoConfigureAfter(PaymentAutoConfiguration::class)
class OrderAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(IOrderAdapterLocator::class)
    fun springBeanOrderAdapterLocator(orderAdapters: ObjectProvider<IOrderAdapter<*>>) : SpringBeanOrderAdapterLocator {
        return SpringBeanOrderAdapterLocator(orderAdapters)
    }

    @Bean
    fun orderErrorsRegistration():OrderErrorsRegistration{
        return OrderErrorsRegistration()
    }

    @Bean
    @ConditionalOnMissingBean(IOrderWorkflow::class)
    fun defaultOrderWorkflow(
        orderPaymentTradeMapper: OrderPaymentTradeMapper,
        transactionTemplate: TransactionTemplate,
        paymentUtils: PaymentUtils,
        idGenerator: IIdGenerator,
        orderAdapterLocator: IOrderAdapterLocator
    ): IOrderWorkflow {
        return DefaultOrderWorkflow(orderPaymentTradeMapper, transactionTemplate,paymentUtils,idGenerator, orderAdapterLocator)
    }

    @Bean
    @ConditionalOnProperty(value= ["application.order.callback-handler.enabled"], matchIfMissing = true)
    @ConditionalOnBean(type=["com.labijie.application.payment.callback.PaymentCallbackMvcInterceptor"])
    fun orderPaymentCallbackHandler(orderWorkflow: IOrderWorkflow): OrderPaymentCallbackHandler {
        return OrderPaymentCallbackHandler(orderWorkflow)
    }

    @Bean
    @ConditionalOnProperty(value= ["application.order.callback-handler.enabled"], matchIfMissing = true)
    @ConditionalOnBean(type=["com.labijie.application.payment.callback.RefundCallbackMvcInterceptor"])
    fun orderRefundCallblackHandler(mapper:OrderPaymentTradeMapper, orderWorkflow: IOrderWorkflow): OrderRefundCallbackHandler {
        return OrderRefundCallbackHandler(mapper,orderWorkflow)
    }
}