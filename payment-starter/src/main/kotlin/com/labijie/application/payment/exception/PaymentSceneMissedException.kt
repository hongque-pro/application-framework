package com.labijie.application.payment.exception

import kotlin.reflect.KClass

/**
 * Author: Anders Xiao
 * Date: Created in 2020/2/27 18:51
 * Copyright: Copyright (c) 2020
 * Github: https://github.com/endink
 */
class PaymentSceneMissedException(provider:String, sceneType: KClass<*>, message:String? = null, cause: Throwable? = null)
    : PaymentException(provider, message?:"Payment provider require a scene value of type '${sceneType.java.simpleName}'.", cause)