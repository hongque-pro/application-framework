package com.labijie.application.payment.testing

import com.labijie.application.payment.configuration.PaymentProperties
import com.labijie.application.payment.providers.wechat.WechatPaymentOptions
import com.labijie.application.payment.providers.wechat.WechatPaymentProvider
import com.labijie.application.web.client.MultiRestTemplates
import com.labijie.infra.spring.configuration.NetworkConfig
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class WechatCarTester : AbstractPaymentProviderTester<WechatPaymentProvider>() {
    override val resourceConfigFile: String
        get() = "wechat.test.json"

    override fun createPaymentProvider(
        networkConfig: NetworkConfig,
        properties: PaymentProperties,
        restTemplates: MultiRestTemplates
    ): WechatPaymentProvider {

        val options = WechatPaymentOptions().also {
            it.appId = this.testConfig.getOrDefault("appId", "")
            it.subAppId = this.testConfig.getOrDefault("subAppId", "")
            it.appSecret = this.testConfig.getOrDefault("appSecret", "")
            it.appAccount = this.testConfig.getOrDefault("appAccount", "")
            it.merchantKey = this.testConfig.getOrDefault("merchantKey", "")
            it.httpClientCertificateFile = "wechat.test.p12"
            it.httpClientCertificatePassword = this.testConfig.getOrDefault("appAccount", "")

        }
        return WechatPaymentProvider(paymentProperties, networkConfig, options, restTemplates)
    }

    override fun buildPlatfromTestParameters(): PlatfromTestParameters {
        return PlatfromTestParameters().also {
            it.platformBuyerId = platformBuyerId
            it.platformMerchantKey = platformMerchantKey
        }
    }

    @Test
    fun testRefundQuery(){
        super.testQueryRefund(
            refundId = "497819362191736832",
            tradeId = "497819362191736832"
        ){
            Assertions.assertNotNull(it)
        }
    }
}