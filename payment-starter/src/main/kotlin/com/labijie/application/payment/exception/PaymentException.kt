package com.labijie.application.payment.exception

import com.labijie.application.ApplicationErrors
import com.labijie.application.ErrorCodedStatusException

open class PaymentException(val provider:String, errorMessage:String, cause: Throwable? = null, errorCode:String = ApplicationErrors.UnhandledError) :
    ErrorCodedStatusException(errorCode, "$errorMessage (provider: $provider)", cause) {

    override fun getDetails(): Map<String, Any>? {
        return mapOf("payment_provider" to this.provider)
    }
}