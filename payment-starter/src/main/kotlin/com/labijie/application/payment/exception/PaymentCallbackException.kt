package com.labijie.application.payment.exception

open class PaymentCallbackException(paymentProvider:String, message:String? = null, cause: Exception? = null)
    : PaymentException(paymentProvider, errorMessage =  message ?: "Payment callback fault (provider: $paymentProvider).", cause = cause)