package com.labijie.application.order.exception

import com.labijie.application.ApplicationErrors
import com.labijie.application.ErrorCodedStatusException

open class OrderException(errorMessage:String, cause: Throwable? = null, errorCode:String = ApplicationErrors.UnhandledError) :
    ErrorCodedStatusException(errorCode,  errorMessage, cause) {
}