package com.labijie.application.order.exception

import kotlin.reflect.KClass

class OrderAdapterNotFoundException constructor(orderType:String) : OrderException("Cant found order adapter for order type: '$orderType' .") {

    constructor(orderType: KClass<*>) : this(orderType.java.simpleName)
}