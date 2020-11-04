package com.labijie.application.payment.testing

import com.labijie.application.payment.car.alipay.AlipayCarPaymentProvider
import com.labijie.application.payment.configuration.PaymentProperties
import com.labijie.application.payment.providers.alipay.AlipayPaymentOptions
import com.labijie.application.payment.providers.alipay.AlipayPaymentProvider
import com.labijie.application.web.client.MultiRestTemplates
import com.labijie.infra.spring.configuration.NetworkConfig
import org.junit.jupiter.api.Test
import org.springframework.web.client.RestTemplate

/**
 * Author: Anders Xiao
 * Date: Created in 2020/3/17 15:58
 * Copyright: Copyright (c) 2020
 * Github: https://github.com/endink
 */
class AlipayCarTester : AbstractPaymentProviderTester<AlipayCarPaymentProvider>(){

    @Test
    fun testQuery(){
        this.testQueryExisted("2003171512350010632460433163", isPlatformTradeId = true)
    }

    override fun createPaymentProvider(
        networkConfig: NetworkConfig,
        properties: PaymentProperties,
        restTemplates: MultiRestTemplates
    ): AlipayCarPaymentProvider {

        val options = AlipayPaymentOptions().apply {
            this.appId = testConfig.getOrDefault("appId", "")
            this.appSecret = testConfig.getOrDefault("appSecret", "")
            this.alipayPubKey = testConfig.getOrDefault("alipayPubKey", "")
            this.appAccount = testConfig.getOrDefault("appAccount", "")
        }

        return AlipayCarPaymentProvider(properties, options, restTemplates)
    }

    override fun buildPlatfromTestParameters(): PlatfromTestParameters {
        return PlatfromTestParameters().apply {
            this.platformBuyerId = testConfig.getOrDefault("platformBuyerId", "")
            this.platformMerchantKey = testConfig.getOrDefault("platformMerchantKey", "")
        }
    }

    override val resourceConfigFile: String
        get() = "alipay-car.test.json"
}