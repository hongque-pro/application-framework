package com.labijie.application.payment.abstraction

import com.labijie.application.payment.*
import com.labijie.application.payment.configuration.PaymentProperties
import com.labijie.application.web.client.MultiRestTemplates
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.validation.annotation.Validated
import java.lang.IllegalArgumentException
import java.net.URI
import javax.validation.Valid


@Validated
abstract class AbstractPaymentProvider<TOptions: PaymentOptions>(
    protected val paymentProperties: PaymentProperties,
    protected val options:TOptions,
    restTemplates: MultiRestTemplates
) : IPaymentProvider {

    protected val restTemplates :MultiRestTemplates = this.onBuildRestTemplate(restTemplates)

    protected val log: Logger = LoggerFactory.getLogger(this::class.java.name.split('$').first())

    override val name: String
        get() = options.providerName

    protected fun onBuildRestTemplate(restTemplate: MultiRestTemplates):MultiRestTemplates{
        return restTemplate
    }

    protected abstract fun doCreateTrade(trade: PlatformTrade): PaymentTradeCreationResult

    abstract override fun queryTrade(@Valid query: PaymentTradeQuery): PaymentTradeQueryResult?

    protected fun checkIsvParameter(parameter:IPlatformIsvParameter){
        //服务商模式时，需要获取签约商户的 key
        if (parameter.mode == TradeMode.ISV && parameter.platformMerchantKey.isBlank()) {
            throw IllegalArgumentException("Payment provider create trade require PlatformTrade.platformMerchantAccount parameter when use isv mode. ( provider: ${options.providerName} )")
        }
    }

    override fun createTrade(@Valid trade: PlatformTrade) : PaymentTradeCreationResult{
        if (trade.mode == TradeMode.ISV) {
            this.checkIsvParameter(trade)
        }
        return this.doCreateTrade(trade)
    }

    protected fun getRefundCallbackUrl(state:String? = null): String {
        val url = this.refundCallbackUrlNoState
        if(!state.isNullOrBlank()){
            return "$url/$state"
        }
        return url
    }

    protected fun getPaymentCallbackUrl(state:String? = null): String {
        val url = this.paymentCallbackUrlNoState
        if(!state.isNullOrBlank()){
            return "$url/$state"
        }
        return url
    }

    private val refundCallbackUrlNoState: String by lazy {
        val base = paymentProperties.callbackBaseUrl
        if(base.isBlank()){
            throw IllegalArgumentException("Require payment callback base url.")
        }
        val combineUrl = "${paymentProperties.callbackBaseUrl.trimEnd('/')}${PaymentProperties.getRefundCallbackPath(options.providerName)}"
        val uri = URI(combineUrl)
        if(!uri.isAbsolute){
            throw IllegalArgumentException("Payment callback base url must be an absolute url (contains schema, host, port).")
        }
        uri.toASCIIString()
    }

    private val paymentCallbackUrlNoState: String by lazy {
        val base = paymentProperties.callbackBaseUrl
        if(base.isBlank()){
            throw IllegalArgumentException("Require payment callback base url.")
        }
        val combineUrl = "${paymentProperties.callbackBaseUrl.trimEnd('/')}${PaymentProperties.getPaymentCallbackPath(options.providerName)}"
        val uri = URI(combineUrl)
        if(!uri.isAbsolute){
            throw IllegalArgumentException("Payment callback base url must be an absolute url (contains schema, host, port).")
        }
        uri.toASCIIString()
    }
}