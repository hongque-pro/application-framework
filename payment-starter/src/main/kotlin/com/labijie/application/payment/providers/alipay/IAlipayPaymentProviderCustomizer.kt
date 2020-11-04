package com.labijie.application.payment.providers.alipay

interface IAlipayPaymentProviderCustomizer {
    /**
     * Callback to customize a [AlipayPaymentProvider] instance.
     * @param alipayPaymentProvider payment provider to customize
     */
    fun customize(alipayPaymentProvider: AlipayPaymentProvider)
}