package com.labijie.application.payment.testing

import com.labijie.application.payment.IPaymentProvider
import com.labijie.application.payment.callback.IPaymentCallbackHandler
import com.labijie.application.payment.callback.PaymentCallbackMvcInterceptor
import com.labijie.application.payment.configuration.PaymentProperties
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * 通过暴露内部方法增加可测试性
 */
class TestedCallbackMvcInterceptor(
    paymentProperties: PaymentProperties,
    providers: List<IPaymentProvider>,
    handlers: List<IPaymentCallbackHandler>
) : PaymentCallbackMvcInterceptor(paymentProperties, providers, handlers) {


    fun prepareHandlingContextForTesting(request: HttpServletRequest): InternalContext?{
        return super.prepareHandlingContext(request)
    }

    fun parseCallbackPayloadForTesting(context:InternalContext, request: HttpServletRequest): Map<String, String> {
        return super.parseCallbackPayload(context, request)
    }
}