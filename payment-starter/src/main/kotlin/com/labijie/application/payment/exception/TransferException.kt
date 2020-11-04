package com.labijie.application.payment.exception

class TransferException(provider: String, message: String? = null, cause:Throwable? = null) : PaymentException(
    provider,
    message ?: "Invalid payment platform signature.",
    cause
)