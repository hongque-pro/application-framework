package com.labijie.application.payment.exception

open class TransferException(provider: String, message: String? = null, cause:Throwable? = null) : PaymentException(
    provider,
    message ?: "There was an error transferring",
    cause
)