package com.labijie.application.order.component

import kotlin.reflect.KClass

interface IOrderAdapterLocator {
    fun <T: Any> findAdapter(orderType: KClass<T>): IOrderAdapter<T>

    fun findAdapter(orderType: String): IOrderAdapter<*>
}