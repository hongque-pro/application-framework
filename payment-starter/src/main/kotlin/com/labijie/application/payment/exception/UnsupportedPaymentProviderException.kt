package com.labijie.application.payment.exception

import com.labijie.application.payment.PaymentErrors.UnsupportedPaymentProvider

class UnsupportedPaymentProviderException(provider:String) : PaymentException(provider, errorMessage = "Payment provider was not found.", errorCode = UnsupportedPaymentProvider) {
}