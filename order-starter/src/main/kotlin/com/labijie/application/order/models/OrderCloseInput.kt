package com.labijie.application.order.models

import kotlin.reflect.KClass

data class OrderCloseInput(var orderId: Long = 0L, var type: KClass<*>? = null)