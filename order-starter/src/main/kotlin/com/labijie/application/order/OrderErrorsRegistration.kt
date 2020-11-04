package com.labijie.application.order

import com.labijie.application.IErrorRegistration
import com.labijie.application.IErrorRegistry

class OrderErrorsRegistration : IErrorRegistration {
    override fun register(registry: IErrorRegistry) {
        registry.registerErrors(OrderErrors)
    }
}