package com.labijie.application.payment.callback

import org.springframework.core.Ordered

interface IPaymentCallbackHandler : Ordered {
    fun handleCallback(context:PaymentCallbackContext)

    override fun getOrder(): Int {
        return 0
    }
}