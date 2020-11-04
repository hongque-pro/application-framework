package com.labijie.application.payment.testing

import com.labijie.application.payment.configuration.PaymentProperties
import com.labijie.application.payment.providers.alipay.AlipayPaymentOptions
import com.labijie.application.payment.providers.alipay.AlipayPaymentProvider
import com.labijie.application.web.client.MultiRestTemplates
import com.labijie.infra.spring.configuration.NetworkConfig
import org.springframework.web.client.RestTemplate
import kotlin.test.Test

/**
 * 资源文件中放置配置文件：alipay.test.json
{
"appId": "",  【服务号的 appid】
"appSecret": "", 【应用私钥】
"alipayPubKey": "", 【支付宝公钥】
"appAccount": "", 【支付宝账号 (isv 商户号)】
"platformBuyerId": "", 【购买用户的支付宝用户 id】
"platformMerchantKey": 【签约商户的  app_auth_token】
}
 */
class AlipayTester : AbstractPaymentProviderTester<AlipayPaymentProvider>() {

    @Test
    fun testQuery(){
        //this.testQueryExisted("434446576149069824", isPlatformTradeId = false)
    }

    override fun createPaymentProvider(
        networkConfig: NetworkConfig,
        properties: PaymentProperties,
        restTemplates: MultiRestTemplates
    ): AlipayPaymentProvider {

        val options = AlipayPaymentOptions().also {
            it.appId = this.testConfig.getOrDefault("appId", "")
            it.appSecret = this.testConfig.getOrDefault("appSecret", "")
            it.alipayPubKey = this.testConfig.getOrDefault("alipayPubKey", "")
            it.appAccount = this.testConfig.getOrDefault("appAccount", "")
        }

        return AlipayPaymentProvider(properties, options, restTemplates)
    }

    override fun buildPlatfromTestParameters(): PlatfromTestParameters {
        return PlatfromTestParameters().also {
            it.platformBuyerId = this.platformBuyerId
            it.platformMerchantKey = this.platformMerchantKey
        }
    }

    override val resourceConfigFile: String
        get() = "alipay.test.json"
}