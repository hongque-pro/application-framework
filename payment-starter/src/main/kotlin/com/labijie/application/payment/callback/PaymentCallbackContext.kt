package com.labijie.application.payment.callback

import com.labijie.application.payment.PaymentCallbackRequest
import org.hibernate.validator.constraints.Length
import javax.servlet.http.HttpServletRequest

class PaymentCallbackContext internal constructor(
    val request:PaymentCallbackRequest,
    paymentProvider: String,
    httpRequest: HttpServletRequest): AbstractCallbackContext(paymentProvider, httpRequest)