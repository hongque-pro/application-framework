package com.labijie.application.order.component

import com.labijie.application.order.exception.OrderAdapterNotFoundException
import com.labijie.application.order.exception.OrderException
import com.labijie.application.payment.configuration.PaymentProperties
import org.springframework.beans.factory.ObjectProvider
import java.util.regex.Pattern
import java.util.stream.Collectors
import kotlin.reflect.KClass


class SpringBeanOrderAdapterLocator(orderAdapterProvider: ObjectProvider<IOrderAdapter<*>>) : IOrderAdapterLocator {
    private val orderTypeNamePattern = Pattern.compile("^${PaymentProperties.DEFAULT_STATE_RULE_REGEX}\$")

    private val typedOrderAdapters: Map<KClass<*>, IOrderAdapter<*>> = this.buildTypedAdapterMap(orderAdapterProvider)
    private val namedOrderAdapters: Map<String, IOrderAdapter<*>> = this.buildNamedAdapterMap(orderAdapterProvider)



    fun buildTypedAdapterMap(orderAdapterProvider: ObjectProvider<IOrderAdapter<*>>): Map<KClass<*>, IOrderAdapter<*>> {
        val list = orderAdapterProvider.stream().collect(Collectors.toList())
        val adapters = list.map {
            it.orderType to it
        }.toList()

        return mapOf(*adapters.toTypedArray())
    }

    private fun buildNamedAdapterMap(orderAdapterProvider: ObjectProvider<IOrderAdapter<*>>): Map<String, IOrderAdapter<*>> {
        val list = orderAdapterProvider.stream().collect(Collectors.toList())
        val adapters = list.map {
            if(!orderTypeNamePattern.matcher(it.orderTypeName).matches()){
                throw OrderException("Only lowercase letters, numbers, - and _ are allowed in IOrderAdapter.orderTypeName .")
            }
            it.orderTypeName to it
        }.toList()

        return mapOf(*adapters.toTypedArray())
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T:Any> findAdapter(orderType: KClass<T>): IOrderAdapter<T> {
        val adapter = typedOrderAdapters[orderType] ?: throw OrderAdapterNotFoundException(orderType)
        return adapter as IOrderAdapter<T>
    }

    override fun findAdapter(orderType: String): IOrderAdapter<*> {
        return namedOrderAdapters[orderType] ?: throw OrderAdapterNotFoundException(orderType)
    }
}