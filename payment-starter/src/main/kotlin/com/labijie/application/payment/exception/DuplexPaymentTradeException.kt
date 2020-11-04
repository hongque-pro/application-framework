package com.labijie.application.payment.exception

import com.labijie.application.payment.PaymentErrors

/**
 * Author: Anders Xiao
 * Date: Created in 2020/2/27 20:57
 * Copyright: Copyright (c) 2020
 * Github: https://github.com/endink
 */
class DuplexPaymentTradeException(provider:String, errorMessage:String? = null, cause: Throwable? = null)
    : PaymentException(provider, errorMessage ?: "Payment trade already existed.", cause, PaymentErrors.DuplexPaymentTrade) {
}