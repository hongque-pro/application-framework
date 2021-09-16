package com.labijie.application.payment.providers.wechat

import com.labijie.application.payment.PaymentExchangeUrls
import com.labijie.application.payment.PaymentOptions
import com.labijie.application.payment.configuration.PaymentAutoConfiguration
import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("${PaymentAutoConfiguration.PROVIDERS_CONFIG_PREFIX}.wechat")
class WechatPaymentOptions : PaymentOptions() {

    override val providerName: String = ProviderName

    var exchange: PaymentExchangeUrls = PaymentExchangeUrls()

    /**
     * 微信服务商模式下实际的应用 app id（如：小程序 appid 、H5 应用 appid
     */
    var subAppId: String = ""

    companion object {
        const val ProviderName = "wechat"
    }

    /**
     * 微信支付用于签名和验签的 api key
     */
    var merchantKey: String = ""

    var certificateSerialNo: String = ""

    var httpClientCertificateFile: String? = null
    var httpClientCertificatePassword: String? = null
}