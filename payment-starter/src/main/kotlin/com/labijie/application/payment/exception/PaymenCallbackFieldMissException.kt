package com.labijie.application.payment.exception

import com.labijie.application.ApplicationRuntimeException
import java.lang.Exception

class PaymenCallbackFieldMissException(paymentProvider:String, parameterName:String, message:String? = null, cause:Exception? = null)
    : PaymentCallbackException(paymentProvider, message ?: "Payment callback field '$parameterName' missed in request data (provider: $paymentProvider).", cause)