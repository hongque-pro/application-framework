package com.labijie.application.payment

import com.labijie.application.IErrorRegistration
import com.labijie.application.IErrorRegistry

class PaymentErrorsRegistration : IErrorRegistration {
    override fun register(registry: IErrorRegistry) {
        registry.registerErrors(PaymentErrors)
    }
}