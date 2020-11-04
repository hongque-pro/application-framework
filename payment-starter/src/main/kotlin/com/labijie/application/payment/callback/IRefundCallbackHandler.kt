package com.labijie.application.payment.callback

import org.springframework.core.Ordered

interface IRefundCallbackHandler : Ordered {
    override fun getOrder(): Int {
        return 0
    }

    fun handleCallback(context:RefundCallbackContext)
}