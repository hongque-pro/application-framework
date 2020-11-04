package com.labijie.application.payment.callback

import com.labijie.application.payment.IPaymentProvider
import com.labijie.application.payment.configuration.PaymentProperties
import javax.servlet.http.HttpServletRequest

open class PaymentCallbackMvcInterceptor(
    paymentProperties: PaymentProperties,
    providers: List<IPaymentProvider>,
    handlers: List<IPaymentCallbackHandler>
) : AbstractCallbackMvcInterceptor<PaymentCallbackContext, IPaymentCallbackHandler>(
    paymentProperties,
    providers,
    handlers
) {
    override val actionNameInUrl: String
        get() = "pay"

    override fun invokeHandler(handler: IPaymentCallbackHandler, context: PaymentCallbackContext) {
        handler.handleCallback(context)
    }

    override fun makeContext(
        provider: IPaymentProvider,
        requestContent: Map<String, String>,
        request: HttpServletRequest
    ): PaymentCallbackContext {
        val callbackRequest = provider.parsePaymentCallbackRequest(requestContent)
        return PaymentCallbackContext(callbackRequest, provider.name, request)
    }
}