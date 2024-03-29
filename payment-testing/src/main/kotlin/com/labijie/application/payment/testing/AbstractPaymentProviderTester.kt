package com.labijie.application.payment.testing

import com.labijie.application.configuration.HttpClientProperties
import com.labijie.application.payment.*
import com.labijie.application.payment.configuration.PaymentProperties
import com.labijie.application.web.client.MultiRestTemplates
import com.labijie.infra.IIdGenerator
import com.labijie.infra.snowflake.ISlotProvider
import com.labijie.infra.snowflake.ISlotProviderFactory
import com.labijie.infra.snowflake.SnowflakeIdGenerator
import com.labijie.infra.snowflake.SnowflakeProperties
import com.labijie.infra.snowflake.providers.StaticSlotProvider
import com.labijie.infra.json.JacksonHelper
import com.labijie.infra.CommonsProperties
import com.labijie.infra.utils.ifNullOrBlank
import org.junit.jupiter.api.Assertions
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.boot.web.client.RestTemplateCustomizer
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.http.converter.StringHttpMessageConverter
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import java.math.BigDecimal

abstract class AbstractPaymentProviderTester<T : IPaymentProvider> {

    protected val paymentProvider: T
    protected val idGenerator: IIdGenerator
    protected val restTemplates: MultiRestTemplates
    protected val paymentProperties = PaymentProperties().apply {
        this.callbackBaseUrl = "http://alipay.zmkira.top:20001"
    }


    protected open val networkIpMask = "*"


    protected val platformBuyerId
        get() = this.testConfig.getOrDefault("platformBuyerId", "")

    protected val platformMerchantKey
        get() = this.testConfig.getOrDefault("platformMerchantKey", "")

    protected val testConfig by lazy {
        val stream = Thread.currentThread().contextClassLoader
            .getResourceAsStream(this.resourceConfigFile)
            ?: throw IllegalArgumentException("Resource ${this.resourceConfigFile} was not found.")

        val bytes = stream.readBytes()
        JacksonHelper.deserializeMap(bytes, String::class, String::class)
    }

    protected abstract val resourceConfigFile: String

    /**
     * 因为涉及支付等问题，要启用单元测试，手动开启此选项
     */
    protected open val testEnabled = true

    protected val networkConfig = CommonsProperties(null).apply {
        this.networks["default"] = networkIpMask
    }

    init {
        val fact: ISlotProviderFactory = object : ISlotProviderFactory {
            override fun createProvider(providerName: String): ISlotProvider {
                return StaticSlotProvider()
            }
        }
        val sc = SnowflakeProperties().apply {
            this.provider = "static"
        }
        idGenerator = SnowflakeIdGenerator(sc, fact)
        restTemplates = this.buildRestTemplate()
        paymentProvider = this.createPaymentProvider(this.networkConfig, this.paymentProperties, restTemplates)
    }


    private fun buildRestTemplate(): MultiRestTemplates {

        val properties = HttpClientProperties().apply {
            loggerEnabled = true
        }

        val customizer = RestTemplateCustomizer {
            it.messageConverters.add(
                0,
                MappingJackson2HttpMessageConverter(JacksonHelper.defaultObjectMapper) as HttpMessageConverter<*>
            )
            it.messageConverters.add(1, StringHttpMessageConverter(Charsets.UTF_8))
        }

        val builder = RestTemplateBuilder(customizer)

        return MultiRestTemplates(builder,properties)
    }

    protected abstract fun createPaymentProvider(
        networkConfig: CommonsProperties,
        properties: PaymentProperties,
        restTemplates: MultiRestTemplates
    ): T

    protected abstract fun buildPlatfromTestParameters(): PlatfromTestParameters

    protected fun printObject(obj: Any?) {
        if (obj != null) {
            println(JacksonHelper.defaultObjectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj))
        }else{
            println("<null object>")
        }
    }

    protected open fun createTrade(mode: TradeMode = TradeMode.Merchant): PlatformTrade {
        val parameters = this.buildPlatfromTestParameters()
        return PlatformTrade().apply {
            this.amount = BigDecimal(0.01)
            this.mode = mode
            this.tradeId = idGenerator.newId().toString()

            this.subject = parameters.subject.ifBlank { "这是一笔测试支付" }
            this.platformBuyerId = parameters.platformBuyerId
            this.platformMerchantKey = parameters.platformMerchantKey

            this.timeoutMinutes = 60 * 5 //测试 5 分钟超时
        }
    }

    open fun testCreateTrade(mode: TradeMode = TradeMode.Merchant) {
        val trade = this.createTrade(mode)
        if (testEnabled) {
            val result = this.paymentProvider.createTrade(trade)
            printObject(result)
        }
    }


    protected fun testCreateOneMoreTime(mode: TradeMode = TradeMode.Merchant) {
        val trade = this.createTrade(mode)
        if (testEnabled) {
            val result1 = this.paymentProvider.createTrade(trade)
            printObject(result1)
            val result2 = this.paymentProvider.createTrade(trade)
            printObject(result2)
        }
    }

    open fun testQueryNotExisted() {
        val parameters = buildPlatfromTestParameters()
        val query =
            PaymentTradeQuery(id = 1123456789.toString(), isPlatformTradeId = false, mode = TradeMode.ISV).apply {
                this.platformMerchantKey = parameters.platformMerchantKey
            }
        if (testEnabled) {
            val result = this.paymentProvider.queryTrade(query)
            Assertions.assertNull(result)
        }
    }

    open fun testQueryRefund(
        refundId: String,
        tradeId: String,
        onCompleted: ((r: RefundResult?) -> Unit)? = null
    ) {
        val query = RefundQuery(
            tradeId = tradeId, refundId = refundId, isPlatformTradeId = false, mode = TradeMode.ISV,
            platformMerchantKey = this.platformMerchantKey
        )
        val r = this.paymentProvider.queryRefund(query)
        printObject(r)
        onCompleted?.invoke(r)
    }

    open fun testRefund(
        refundId: String,
        tradeId: String,
        orderAmount: BigDecimal,
        remark: String? = null,
        onCompleted: ((r: RefundResult) -> Unit)? = null
    ) {
        if (testEnabled) {
            val trade = RefundTrade(
                refundId = refundId,
                isPaymentTradeId = false,
                paymentTradeId = tradeId,
                amount = orderAmount,
                platformMerchantKey = this.platformMerchantKey,
                remark = remark,
                mode = TradeMode.ISV
            )

            val r = this.paymentProvider.refund(trade)
            printObject(r)
            onCompleted?.invoke(r)
        }
    }

    open fun testQueryExisted(tradeId: String? = null, isPlatformTradeId: Boolean = false) {
        if (testEnabled) {
            val trade = this.createTrade()
            //先创建一个支付交易
            val id = tradeId ?: this.paymentProvider.createTrade(trade).tradeId

            val parameters = buildPlatfromTestParameters()
            val isPlatform = if (tradeId.isNullOrBlank()) true else isPlatformTradeId
            val query = PaymentTradeQuery(id, isPlatformTradeId = isPlatform, mode = TradeMode.ISV).apply {
                this.platformMerchantKey = parameters.platformMerchantKey
            }

            val queryResult = this.paymentProvider.queryTrade(query)
            printObject(queryResult)
            Assertions.assertNotNull(queryResult)
        }
    }

    open fun testTransfer(
        tradeId: String? = null,
        onCompleted: ((r: TransferTradeResult?) -> Unit)? = null
    ){
        if (testEnabled) {
            val parameters = this.buildPlatfromTestParameters()

            val transferTrade = TransferTrade(
                tradeId.ifNullOrBlank { idGenerator.newId().toString() },
                parameters.platformBuyerId,
                BigDecimal(1),
                parameters.buyerRealName,
                "付款测试",
                "这是一笔金额为 ${0.01} 的付款测试"
            )
            val result = this.paymentProvider.transfer(transferTrade)
            onCompleted?.invoke(result)
        }
    }

    open fun testQueryTransfer(tradeId: String, isPlatformTradeId: Boolean = false,
                               onCompleted: ((r: TransferQueryResult?) -> Unit)? = null){
        if (testEnabled) {
            val transferTrade = TransferQuery(
                tradeId,
                isPlatformTradeId
            )
            val result = this.paymentProvider.queryTransfer(transferTrade)
            onCompleted?.invoke(result)
        }
    }
}