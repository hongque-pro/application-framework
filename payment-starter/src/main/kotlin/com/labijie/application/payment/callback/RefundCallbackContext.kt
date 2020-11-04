package com.labijie.application.payment.callback

import com.labijie.application.payment.RefundResult
import javax.servlet.http.HttpServletRequest

class RefundCallbackContext(
    val result: RefundResult,
    paymentProvider: String,
    httpRequest: HttpServletRequest
) :AbstractCallbackContext(paymentProvider, httpRequest)