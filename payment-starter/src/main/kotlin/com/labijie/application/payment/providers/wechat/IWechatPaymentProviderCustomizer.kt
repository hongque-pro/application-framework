package com.labijie.application.payment.providers.wechat

interface IWechatPaymentProviderCustomizer {

    /**
     * Callback to customize a [WechatPaymentProvider] instance.
     * @param wechatPaymentProvider payment provider to customize
     */
    fun customize(wechatPaymentProvider: WechatPaymentProvider)

}