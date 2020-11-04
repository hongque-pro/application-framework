package com.labijie.application.payment.configuration

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("application.pay")
class PaymentProperties {
    companion object{
        const val VERSION:String = "1"
        const val DEFAULT_STATE_RULE_REGEX = "[a-z0-9_-]*"
        const val PATH_PATTERN = "/callback/pay/**"
        const val PATH_REFUND_PATTERN = "/callback/pay_refund/**"

        @JvmStatic
        fun getPaymentCallbackPath(provider:String): String {
            return "/callback/pay/v$VERSION/$provider"
        }

        @JvmStatic
        fun getRefundCallbackPath(provider:String): String {
            return "/callback/pay_refund/v$VERSION/$provider"
        }
    }
    var callbackBaseUrl = "http://localhost"
}