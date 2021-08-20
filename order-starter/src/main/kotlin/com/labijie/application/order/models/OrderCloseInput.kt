package com.labijie.application.order.models

import kotlin.reflect.KClass

data class OrderCloseInput<T:Any>(var orderId: Long = 0L, var type: KClass<T>? = null)