package com.labijie.application.payment.exception

class PaymentExchangeException(
    provider: String,
    message: String? = null,
    cause: Throwable? = null,
    val platformErrorCode:String? = null
) : PaymentException(
    provider,
    message ?: "Exchange with payment platform fault ${if(platformErrorCode.isNullOrBlank()) "" else "platform error code: $platformErrorCode"}.",
    cause
)