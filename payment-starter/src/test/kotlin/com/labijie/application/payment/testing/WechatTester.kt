package com.labijie.application.payment.testing

import com.labijie.application.payment.configuration.PaymentProperties
import com.labijie.application.payment.providers.wechat.WechatPaymentOptions
import com.labijie.application.payment.providers.wechat.WechatPaymentProvider
import com.labijie.application.web.client.MultiRestTemplates
import com.labijie.infra.spring.configuration.NetworkConfig
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.math.BigDecimal

/**
 * 资源文件中放置配置文件：wechat.test.json
{
"appId": "", 【服务号的 appid】
"subAppId": "", 【实际应用 (小程序) 的 appid】
"appSecret": "", 【服务号的 APP key】
"merchantKey": "", 【ISV 商户 key，非 ISV 就是自己的商户 key】
"appAccount": "", 【商户号 (isv 商户号)】
"platformBuyerId": "", 【买家 open id】
"platformMerchantKey": "" 【签约商户的 sub_mnt_id】
}
 */
class WechatTester : AbstractPaymentProviderTester<WechatPaymentProvider>() {

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
    override fun testCreateOneMoreTime() {
        super.testCreateOneMoreTime()
    }

    @Test
    override fun testQueryNotExisted() {
        super.testQueryNotExisted()
    }

    @Test
    fun testQueryExisted() {
        super.testQueryExisted("430792225807400960", false)
    }

    @Test
    fun testRefund(){
        super.testRefund(
            refundId = "496031690309763072",
            tradeId = "496031690309763072", orderAmount = BigDecimal("0.01"),
            remark = "退款接口测试"
        ){
            Assertions.assertNotNull(it)
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