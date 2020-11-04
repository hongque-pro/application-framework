package com.labijie.application.payment.providers.alipay

import com.labijie.application.payment.PaymentOptions
import com.labijie.application.payment.configuration.PaymentAutoConfiguration.Companion.PROVIDERS_CONFIG_PREFIX
import com.labijie.application.payment.providers.wechat.WechatPaymentOptions
import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("$PROVIDERS_CONFIG_PREFIX.alipay")
class AlipayPaymentOptions : PaymentOptions() {

    override val providerName: String = ProviderName

    var exchangeUrl:String = "https://openapi.alipay.com/gateway.do"

    companion object{
        const val ProviderName = "alipay"
    }

    var alipayPubKey:String = ""
}