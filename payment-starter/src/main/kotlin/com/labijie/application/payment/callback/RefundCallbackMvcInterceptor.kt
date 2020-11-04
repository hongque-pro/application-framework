package com.labijie.application.payment.callback

import com.labijie.application.payment.IPaymentProvider
import com.labijie.application.payment.configuration.PaymentProperties
import javax.servlet.http.HttpServletRequest

class RefundCallbackMvcInterceptor(
    paymentProperties: PaymentProperties,
    providers: List<IPaymentProvider>,
    handlers: List<IRefundCallbackHandler>
) : AbstractCallbackMvcInterceptor<RefundCallbackContext, IRefundCallbackHandler>(
    paymentProperties,
    providers,
    handlers
) {
    override val actionNameInUrl: String
        get() = "pay_refund"

    override fun invokeHandler(handler: IRefundCallbackHandler, context: RefundCallbackContext) {
        handler.handleCallback(context)
    }

    override fun makeContext(
        provider: IPaymentProvider,
        requestContent: Map<String, String>,
        request: HttpServletRequest
    ): RefundCallbackContext {
        val result = provider.parserRefundCallbackRequest(requestContent)
        return RefundCallbackContext(result, provider.name, request)
    }
}